package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.user.FriendEntity
import de.ashman.ontrack.entity.user.FriendRequestEntity
import de.ashman.ontrack.entity.user.UserEntity
import de.ashman.ontrack.features.feed.friend.toFriendEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlin.collections.map

interface FriendService {
    suspend fun searchForNewFriends(query: String): List<FriendEntity>
    suspend fun getFriends(): Flow<List<FriendEntity>>
    suspend fun getReceivedRequests(): Flow<List<FriendRequestEntity>>
    suspend fun getSentRequests(): Flow<List<FriendRequestEntity>>

    suspend fun removeFriend(friend: Friend)

    suspend fun sendRequest(otherRequest: FriendRequest, myRequest: FriendRequest)
    suspend fun acceptRequest(friendRequest: FriendRequest)
    suspend fun declineRequest(friendRequest: FriendRequest)
    suspend fun cancelRequest(friendRequest: FriendRequest)
}

class FriendServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FriendService {
    private val userCollection = firestore.collection("users")

    override suspend fun searchForNewFriends(query: String): List<FriendEntity> {
        val results = mutableListOf<FriendEntity>()

        // TODO definitely other way
        val existingFriends = userCollection
            .document(authService.currentUserId)
            .collection("friends")
            .get()
            .documents.map { it.id }

        val existingReceived = userCollection
            .document(authService.currentUserId)
            .collection("receivedRequests")
            .get()
            .documents.map { it.id }

        val existingSent = userCollection
            .document(authService.currentUserId)
            .collection("sentRequests")
            .get()
            .documents.map { it.id }

        val notVisibleUsers = existingFriends + existingReceived + existingSent

        // Search for users matching the query
        val snapshot = userCollection
            .where { "username" greaterThanOrEqualTo query }
            .where { "username" lessThan query + "z" }
            .get()

        for (document in snapshot.documents) {
            val user = document.data<UserEntity>()
            if (user.id != authService.currentUserId && user.id !in notVisibleUsers) {
                results.add(
                    FriendEntity(
                        id = user.id,
                        username = user.username,
                        name = user.displayName,
                        imageUrl = user.imageUrl,
                    )
                )
            }
        }

        return results
    }

    override suspend fun getFriends(): Flow<List<FriendEntity>> {
        return userCollection.document(authService.currentUserId)
            .collection("friends")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendEntity>() } }
    }

    override suspend fun getReceivedRequests(): Flow<List<FriendRequestEntity>> {
        return userCollection.document(authService.currentUserId)
            .collection("receivedRequests")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>() } }
    }

    override suspend fun getSentRequests(): Flow<List<FriendRequestEntity>> {
        return userCollection.document(authService.currentUserId)
            .collection("sentRequests")
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>() } }
    }

    override suspend fun removeFriend(friend: Friend) {
        userCollection.document(authService.currentUserId)
            .collection("friends").document(friend.id).delete()

        userCollection.document(friend.id)
            .collection("friends").document(authService.currentUserId).delete()
    }

    override suspend fun sendRequest(friendRequest: FriendRequest, myRequest: FriendRequest) {
        userCollection.document(authService.currentUserId)
            .collection("sentRequests")
            .document(friendRequest.userId).set(friendRequest.toEntity())

        userCollection.document(friendRequest.userId)
            .collection("receivedRequests")
            .document(authService.currentUserId).set(myRequest.toEntity())
    }

    override suspend fun acceptRequest(friendRequest: FriendRequest) {
        userCollection.document(authService.currentUserId)
            .collection("receivedRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("sentRequests")
            .document(authService.currentUserId).delete()

        val friendRequestEntity = friendRequest.toFriendEntity()
        userCollection.document(authService.currentUserId)
            .collection("friends").document(friendRequestEntity.id).set(friendRequestEntity)

        val myself = FriendEntity(
            id = authService.currentUserId,
            username = authService.currentUserName,
            name = authService.currentUserName,
            imageUrl = authService.currentUserImage
        )
        userCollection.document(friendRequest.userId)
            .collection("friends").document(authService.currentUserId).set(myself)
    }

    override suspend fun declineRequest(friendRequest: FriendRequest) {
        userCollection.document(authService.currentUserId)
            .collection("receivedRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("sentRequests")
            .document(authService.currentUserId).delete()
    }

    override suspend fun cancelRequest(friendRequest: FriendRequest) {
        userCollection.document(authService.currentUserId)
            .collection("sentRequests")
            .document(friendRequest.userId).delete()

        userCollection.document(friendRequest.userId)
            .collection("receivedRequests")
            .document(authService.currentUserId).delete()
    }
}
