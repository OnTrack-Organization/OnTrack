package de.ashman.ontrack.repository.firestore

import co.touchlab.kermit.Logger
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.FriendRequestStatus
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.user.FriendEntity
import de.ashman.ontrack.entity.user.FriendRequestEntity
import de.ashman.ontrack.entity.user.UserEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

interface FriendRepository {
    suspend fun searchForNewFriends(query: String, existingFriendsAndRequestIds: List<String>): List<Friend>
    suspend fun observeFriends(): Flow<List<Friend>>
    suspend fun observeReceivedRequests(): Flow<List<FriendRequest>>
    suspend fun observeSentRequests(): Flow<List<FriendRequest>>

    suspend fun removeFriend(friend: Friend)

    suspend fun sendRequest(friendRequest: FriendRequest)
    suspend fun acceptRequest(friendRequest: FriendRequest)
    suspend fun declineRequest(friendRequest: FriendRequest)
    suspend fun cancelRequest(friendRequest: FriendRequest)

    suspend fun getFriendStatus(friendId: String): FriendRequestStatus
}

class FriendRepositoryImpl(
    firestore: FirebaseFirestore,
    private val userDataStore: UserDataStore,
) : FriendRepository {
    private val userCollection = firestore.collection("users")

    override suspend fun searchForNewFriends(query: String, excludedIds: List<String>): List<Friend> {
        val results = mutableListOf<Friend>()

        val normalizedQuery = query.trim().lowercase()

        val snapshot = userCollection
            .where { "username" greaterThanOrEqualTo normalizedQuery }
            .where { "username" lessThan normalizedQuery + "z" }
            .where { "id" notInArray excludedIds }
            .get()

        val readCount = snapshot.documents.size
        Logger.d("Firestore reads for this query: $readCount")

        for (document in snapshot.documents) {
            val user = document.data<UserEntity>()

            if (user.id !in excludedIds) {
                results.add(
                    Friend(
                        id = user.id,
                        username = user.username,
                        name = user.name,
                        imageUrl = user.imageUrl,
                    )
                )
            }
        }

        return results
    }

    override suspend fun observeFriends(): Flow<List<Friend>> {
        return userCollection.document(userDataStore.getCurrentUserId())
            .collection("friends")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendEntity>().toDomain() } }
    }

    override suspend fun observeReceivedRequests(): Flow<List<FriendRequest>> {
        return userCollection.document(userDataStore.getCurrentUserId())
            .collection("receivedRequests")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>().toDomain() } }
    }

    override suspend fun observeSentRequests(): Flow<List<FriendRequest>> {
        return userCollection.document(userDataStore.getCurrentUserId())
            .collection("sentRequests")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>().toDomain() } }
    }

    override suspend fun removeFriend(friend: Friend) {
        userCollection.document(userDataStore.getCurrentUserId())
            .collection("friends").document(friend.id).delete()

        userCollection.document(friend.id)
            .collection("friends").document(userDataStore.getCurrentUserId()).delete()
    }

    override suspend fun sendRequest(friendRequest: FriendRequest) {
        userCollection.document(userDataStore.getCurrentUserId())
            .collection("sentRequests")
            .document(friendRequest.userId).set(friendRequest.toEntity())

        val myself = currentUserToFriendRequestEntity()
        userCollection.document(friendRequest.userId)
            .collection("receivedRequests")
            .document(userDataStore.getCurrentUserId()).set(myself)
    }

    override suspend fun declineRequest(friendRequest: FriendRequest) {
        userCollection.document(userDataStore.getCurrentUserId())
            .collection("receivedRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("sentRequests")
            .document(userDataStore.getCurrentUserId()).delete()
    }

    override suspend fun cancelRequest(friendRequest: FriendRequest) {
        userCollection.document(userDataStore.getCurrentUserId())
            .collection("sentRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("receivedRequests")
            .document(userDataStore.getCurrentUserId()).delete()
    }

    // TODO this is ugly af rn
    override suspend fun getFriendStatus(friendId: String): FriendRequestStatus {
        val currentUserId = userDataStore.getCurrentUserId()

        // 1. Check if already friends
        val friendDoc = userCollection.document(currentUserId)
            .collection("friends")
            .document(friendId)
            .get()

        if (friendDoc.exists) {
            return FriendRequestStatus.ACCEPTED
        }

        // 2. Check if you have sent them a request (waiting for them to accept)
        val sentRequestDoc = userCollection.document(currentUserId)
            .collection("sentRequests")
            .document(friendId)
            .get()

        if (sentRequestDoc.exists) {
            return FriendRequestStatus.PENDING
        }

        // 3. Check if you have received a request from them (waiting for you to accept)
        val receivedRequestDoc = userCollection.document(currentUserId)
            .collection("receivedRequests")
            .document(friendId)
            .get()

        if (receivedRequestDoc.exists) {
            return FriendRequestStatus.RECEIVED
        }

        // 4. No relationship found
        return FriendRequestStatus.NONE
    }


    override suspend fun acceptRequest(friendRequest: FriendRequest) {
        val currentUserId = userDataStore.getCurrentUserId()

        // remove from sentRequests and receivedRequests
        userCollection.document(userDataStore.getCurrentUserId())
            .collection("receivedRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("sentRequests")
            .document(currentUserId).delete()

        // add friend to my friends
        val friendRequestEntity = friendRequest.toFriendEntity()
        userCollection.document(currentUserId)
            .collection("friends").document(friendRequestEntity.id).set(friendRequestEntity)

        // add myself to friend's friends
        val myself = currentUserToFriendEntity()
        userCollection.document(friendRequest.userId)
            .collection("friends").document(myself.id).set(myself)
    }

    private suspend fun currentUserToFriendEntity(): FriendEntity {
        val currentUser = userDataStore.getCurrentUser()
        return FriendEntity(
            id = currentUser.id,
            username = currentUser.username,
            name = currentUser.name,
            imageUrl = currentUser.profilePictureUrl,
        )
    }

    private suspend fun currentUserToFriendRequestEntity(): FriendRequestEntity {
        val currentUser = userDataStore.getCurrentUser()
        return FriendRequestEntity(
            userId = currentUser.id,
            username = currentUser.username,
            name = currentUser.name,
            imageUrl = currentUser.profilePictureUrl,
        )
    }

    private fun FriendRequest.toFriendEntity() = FriendEntity(
        id = userId,
        username = username,
        name = name,
        imageUrl = imageUrl,
    )
}
