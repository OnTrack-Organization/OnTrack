package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingHistoryEntryEntity
import de.ashman.ontrack.db.entity.TrackingLikeEntity
import de.ashman.ontrack.db.entity.UserEntity
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

    override suspend fun addFriend(friendId: String) {
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayUnion(friendId)
        )
    }

    override suspend fun removeFriend(friendId: String) {
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayRemove(friendId)
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
    override suspend fun getTrackingFeed(lastTimestamp: Long?): Flow<List<TrackingEntity>> {
        val currentUserId = authService.currentUserId
        val friends = userCollection.document(currentUserId).get().data<UserEntity>().friends + currentUserId

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
                    .take(10)
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
