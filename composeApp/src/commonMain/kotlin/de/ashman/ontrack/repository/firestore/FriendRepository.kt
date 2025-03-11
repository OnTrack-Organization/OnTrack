package de.ashman.ontrack.repository.firestore

import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.toFriendEntity
import de.ashman.ontrack.entity.user.FriendEntity
import de.ashman.ontrack.entity.user.FriendRequestEntity
import de.ashman.ontrack.entity.user.UserEntity
import de.ashman.ontrack.repository.CurrentUserRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

interface FriendRepository {
    suspend fun searchForNewFriends(query: String, existingFriendsAndRequestIds: List<String>): List<Friend>
    suspend fun observeFriends(): Flow<List<Friend>>
    suspend fun observeReceivedRequests(): Flow<List<FriendRequest>>
    suspend fun observeSentRequests(): Flow<List<FriendRequest>>

    suspend fun removeFriend(friend: Friend)

    suspend fun sendRequest(otherRequest: FriendRequest, myRequest: FriendRequest)
    suspend fun acceptRequest(friendRequest: FriendRequest)
    suspend fun declineRequest(friendRequest: FriendRequest)
    suspend fun cancelRequest(friendRequest: FriendRequest)
}

class FriendRepositoryImpl(
    firestore: FirebaseFirestore,
    private val currentUserRepository: CurrentUserRepository,
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
        return userCollection.document(currentUserRepository.currentUserId)
            .collection("friends")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendEntity>().toDomain() } }
    }

    override suspend fun observeReceivedRequests(): Flow<List<FriendRequest>> {
        return userCollection.document(currentUserRepository.currentUserId)
            .collection("receivedRequests")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>().toDomain() } }
    }

    override suspend fun observeSentRequests(): Flow<List<FriendRequest>> {
        return userCollection.document(currentUserRepository.currentUserId)
            .collection("sentRequests")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>().toDomain() } }
    }

    override suspend fun removeFriend(friend: Friend) {
        userCollection.document(currentUserRepository.currentUserId)
            .collection("friends").document(friend.id).delete()

        userCollection.document(friend.id)
            .collection("friends").document(currentUserRepository.currentUserId).delete()
    }

    override suspend fun sendRequest(friendRequest: FriendRequest, myRequest: FriendRequest) {
        userCollection.document(currentUserRepository.currentUserId)
            .collection("sentRequests")
            .document(friendRequest.userId).set(friendRequest.toEntity())

        userCollection.document(friendRequest.userId)
            .collection("receivedRequests")
            .document(currentUserRepository.currentUserId).set(myRequest.toEntity())
    }

    override suspend fun declineRequest(friendRequest: FriendRequest) {
        userCollection.document(currentUserRepository.currentUserId)
            .collection("receivedRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("sentRequests")
            .document(currentUserRepository.currentUserId).delete()
    }

    override suspend fun cancelRequest(friendRequest: FriendRequest) {
        userCollection.document(currentUserRepository.currentUserId)
            .collection("sentRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("receivedRequests")
            .document(currentUserRepository.currentUserId).delete()
    }

    override suspend fun acceptRequest(friendRequest: FriendRequest) {
        userCollection.document(currentUserRepository.currentUserId)
            .collection("receivedRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("sentRequests")
            .document(currentUserRepository.currentUserId).delete()

        val friendRequestEntity = friendRequest.toFriendEntity()
        userCollection.document(currentUserRepository.currentUserId)
            .collection("friends").document(friendRequestEntity.id).set(friendRequestEntity)

        val currentUser = currentUserRepository.getCurrentUser()
        val myself = FriendEntity(
            id = currentUser.id,
            username = currentUser.username,
            name = currentUser.name,
            imageUrl = currentUser.imageUrl,
        )

        userCollection.document(friendRequest.userId)
            .collection("friends").document(currentUserRepository.currentUserId).set(myself)
    }
}
