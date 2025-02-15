package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.FriendEntity
import de.ashman.ontrack.db.entity.FriendRequestEntity
import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingHistoryEntryEntity
import de.ashman.ontrack.db.entity.TrackingLikeEntity
import de.ashman.ontrack.db.entity.UserEntity
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.toEntity
import dev.gitlive.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FirestoreServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FirestoreService {
    private val userCollection = firestore.collection("users")

    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    // USER
    override suspend fun getUserById(userId: String): UserEntity? {
        return userCollection.document(userId).get().data()
    }

    override suspend fun searchForNewFriend(query: String): List<FriendEntity> {
        val results = mutableListOf<FriendEntity>()

        val snapshot = userCollection
            .where { "username" greaterThanOrEqualTo query }
            .where { "username" lessThan query + "z" }
            .get()

        for (document in snapshot.documents) {
            val user = document.data<UserEntity>()
            if (user.id != authService.currentUserId) {
                results.add(
                    FriendEntity(
                        id = user.id,
                        username = user.username,
                        name = user.username,
                        imageUrl = user.imageUrl.orEmpty(),
                    )
                )
            }
        }
        return results
    }

    override suspend fun getFriends(): List<FriendEntity> {
        val snapshot = userCollection.document(authService.currentUserId).get()
        val user = snapshot.data<UserEntity>()
        return user.friends
    }

    override suspend fun getReceivedRequests(): List<FriendRequestEntity> {
        val query = userCollection.document(authService.currentUserId).get()
        val snapshot = query.data<UserEntity>()
        return snapshot.receivedRequests
    }

    override suspend fun getSentRequests(): List<FriendRequestEntity> {
        val query = userCollection.document(authService.currentUserId).get()
        val snapshot = query.data<UserEntity>()
        return snapshot.sentRequests
    }

    override suspend fun removeFriend(friend: Friend) {
        // Remove person from own friend list
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayRemove(friend.toEntity())
        )

        // Remove myself from other persons friend list
        val myself = Friend(
            id = authService.currentUserId,
            username = authService.currentUserName,
            name = authService.currentUserName,
            imageUrl = authService.currentUserImage,
        )

        userCollection.document(friend.id).update(
            "friends" to FieldValue.arrayRemove(myself.toEntity())
        )
    }

    override suspend fun sendRequest(otherRequest: FriendRequest, myRequest: FriendRequest) {
        userCollection.document(authService.currentUserId).update(
            "sentRequests" to FieldValue.arrayUnion(otherRequest.toEntity())
        )

        userCollection.document(otherRequest.userId).update(
            "receivedRequests" to FieldValue.arrayUnion(myRequest.toEntity())
        )
    }

    override suspend fun acceptRequest(friendRequest: FriendRequest) {
        // Remove own received request
        userCollection.document(authService.currentUserId).update(
            "receivedRequests" to FieldValue.arrayRemove(friendRequest.toEntity())
        )

        // Add friend to own friend list
        val friend = FriendEntity(
            id = friendRequest.userId,
            username = friendRequest.username,
            name = friendRequest.name,
            imageUrl = friendRequest.imageUrl,
        )
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayUnion(friend)
        )

        // Remove other persons sent request
        val myRequest = FriendRequestEntity(
            userId = authService.currentUserId,
            username = authService.currentUserName,
            name = authService.currentUserName,
            imageUrl = authService.currentUserImage,

        )

        userCollection.document(friendRequest.userId).update(
            "sentRequests" to FieldValue.arrayRemove(myRequest)
        )

        // Add myself to other persons friend list
        val myself = FriendEntity(
            id = authService.currentUserId,
            username = authService.currentUserName,
            // TODO i guess name und so muss von woanders her
            name = authService.currentUserName,
            imageUrl = authService.currentUserImage,
        )
        userCollection.document(friendRequest.userId).update(
            "friends" to FieldValue.arrayUnion(myself)
        )
    }

    override suspend fun denyRequest(friendRequest: FriendRequest) {
        // Remove own received request
        userCollection.document(authService.currentUserId).update(
            "receivedRequests" to FieldValue.arrayRemove(friendRequest.toEntity())
        )

        // Remove other persons sent request
        userCollection.document(friendRequest.userId).update(
            "sentRequests" to FieldValue.arrayRemove(friendRequest.toEntity())
        )
    }

    override suspend fun cancelRequest(friendId: String, friendRequest: FriendRequest) {
        // Remove own sent request
        userCollection.document(authService.currentUserId).update(
            "sentRequests" to FieldValue.arrayRemove(friendRequest.toEntity())
        )

        // Remove other persons received request
        userCollection.document(friendId).update(
            "receivedRequests" to FieldValue.arrayRemove(friendRequest.toEntity())
        )
    }

    // TRACKING
    override suspend fun saveTracking(tracking: TrackingEntity) {
        val trackingRef = userTrackingCollection(authService.currentUserId).document(tracking.id)

        // maybe get from viewmodel instead of here
        val existingTracking = if (trackingRef.get().exists) {
            trackingRef.get().data<TrackingEntity>()
        } else {
            null
        }

        val updatedHistory = existingTracking?.let {
            if (it.status != tracking.status && it.status != null) {
                it.history + TrackingHistoryEntryEntity(status = it.status, timestamp = it.timestamp)
            } else {
                it.history
            }
        } ?: emptyList()

        trackingRef.set(
            data = tracking.copy(history = updatedHistory),
            merge = true,
        )
    }

    override suspend fun deleteTracking(trackingId: String) {
        userTrackingCollection(authService.currentUserId)
            .document(trackingId)
            .delete()
    }

    override fun fetchTracking(trackingId: String): Flow<TrackingEntity?> {
        return userTrackingCollection(authService.currentUserId)
            .document(trackingId)
            .snapshots
            .map { snapshot ->
                if (!snapshot.exists) {
                    Logger.i("Document does not exist for id: $trackingId")
                    null
                } else {
                    Logger.d("Document fetched for id: $trackingId")
                    snapshot.data<TrackingEntity>()
                }
            }
    }

    override fun fetchTrackings(userId: String): Flow<List<TrackingEntity>> {
        return userTrackingCollection(userId)
            .snapshots
            .mapNotNull { snapshot ->
                snapshot.documents.map { it.data<TrackingEntity>() }
            }
    }

    override suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>> {
        val currentUserId = authService.currentUserId

        val friends = userCollection.document(currentUserId).get().data<UserEntity>().friends.map { it.id } + currentUserId

        val allFlows = friends.map { friendId ->
            userTrackingCollection(friendId)
                .where { "mediaId" equalTo mediaId }
                .snapshots()
                .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
        }

        return if (allFlows.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(allFlows) { trackingLists ->
                trackingLists
                    .toList()
                    .flatten()
                    .distinctBy { it.id }
                    .sortedByDescending { it.timestamp }
            }
        }
    }

    // FEED
    // ONE TIME FETCH
    /*override suspend fun getTrackingFeed(lastTimestamp: Long?): Flow<List<TrackingEntity>> = flow {
        val currentUserId = authService.currentUserId
        val friends = userCollection.document(currentUserId).get().data<UserEntity>().friends + currentUserId

        val allTrackings = mutableListOf<TrackingEntity>()

        for (friendId in friends) {
            var query = userTrackingCollection(friendId)
                .orderBy("timestamp", Direction.DESCENDING)

            if (lastTimestamp != null) {
                query = query.startAfter(lastTimestamp)
            }

            val snapshot = query.get()
            val trackings = snapshot.documents.map { it.data<TrackingEntity>() }
            allTrackings.addAll(trackings)
        }

        emit(allTrackings.sortedByDescending { it.timestamp }.take(10))
    }*/

    // LIVE
    override suspend fun getTrackingFeed(lastTimestamp: Long?, limit: Int): Flow<List<TrackingEntity>> {
        val currentUserId = authService.currentUserId
        val friends = userCollection.document(currentUserId).get().data<UserEntity>().friends.map { it.id } + currentUserId

        val allFlows = friends.map { friendId ->
            var query = userTrackingCollection(friendId)
                .orderBy("timestamp", Direction.DESCENDING)

            if (lastTimestamp != null) {
                query = query.startAfter(lastTimestamp)
            }

            query.snapshots()
                .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
        }

        return if (allFlows.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(allFlows) { trackingLists ->
                trackingLists
                    .toList()
                    .flatten()
                    .distinctBy { it.id }
                    .sortedByDescending { it.timestamp }
                    .take(limit)
            }
        }
    }

    override suspend fun likeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayUnion(like)
            )
    }

    override suspend fun unlikeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayRemove(like)
            )
    }

    override suspend fun addComment(friendId: String, trackingId: String, comment: TrackingCommentEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayUnion(comment)
            )
    }

    override suspend fun deleteComment(friendId: String, trackingId: String, comment: TrackingCommentEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayRemove(comment)
            )
    }
}
