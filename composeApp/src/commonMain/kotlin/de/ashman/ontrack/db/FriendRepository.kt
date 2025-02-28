package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.user.FriendEntity
import de.ashman.ontrack.db.entity.user.FriendRequestEntity
import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.db.entity.user.UserEntity
import de.ashman.ontrack.domain.user.FriendRequest
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlin.collections.map

interface FriendRepository {
    suspend fun searchForNewFriends(query: String): List<FriendEntity>
    suspend fun getFriends(): Flow<List<FriendEntity>>
    suspend fun getReceivedRequests(): Flow<List<FriendRequestEntity>>
    suspend fun getSentRequests(): Flow<List<FriendRequestEntity>>

    suspend fun removeFriend(friendId: String)

    suspend fun sendRequest(otherRequest: FriendRequest, myRequest: FriendRequest)
    suspend fun acceptRequest(friendRequest: FriendRequest)
    suspend fun declineRequest(friendRequest: FriendRequest)
    suspend fun cancelRequest(friendRequest: FriendRequest)
}

class FriendRepositoryImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FriendRepository {
    private val usersCollection = firestore.collection("users")
    private fun friendsCollection(userId: String) = usersCollection.document(userId).collection("friends")
    private fun receivedCollection(userId: String) = usersCollection.document(userId).collection("receivedRequests")
    private fun sentCollection(userId: String) = usersCollection.document(userId).collection("sentRequests")

    override suspend fun searchForNewFriends(query: String): List<FriendEntity> {
        val results = mutableListOf<FriendEntity>()

        val existingFriends = friendsCollection(authService.currentUserId)
            .get()
            .documents.map { it.id }

        val existingReceived = receivedCollection(authService.currentUserId)
            .get()
            .documents.map { it.id }

        val existingSent = sentCollection(authService.currentUserId)
            .get()
            .documents.map { it.id }

        val notVisibleUsers = existingFriends + existingReceived + existingSent

        // Search for users matching the query
        val snapshot = usersCollection
            .where { "username" greaterThanOrEqualTo query }
            .where { "username" lessThan query + "z" }
            .get()

        for (document in snapshot.documents) {
            val user = document.data<UserEntity>()
            if (user.userData.id != authService.currentUserId && user.userData.id !in notVisibleUsers) {
                results.add(
                    FriendEntity(
                        userData = user.userData
                    )
                )
            }
        }

        return results
    }

    override suspend fun getFriends(): Flow<List<FriendEntity>> {
        return friendsCollection(authService.currentUserId)
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendEntity>() } }
    }

    override suspend fun getReceivedRequests(): Flow<List<FriendRequestEntity>> {
        return receivedCollection(authService.currentUserId)
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>() } }
    }

    override suspend fun getSentRequests(): Flow<List<FriendRequestEntity>> {
        return sentCollection(authService.currentUserId)
            .snapshots
            .mapNotNull { it.documents.map { it.data<FriendRequestEntity>() } }
    }

    override suspend fun removeFriend(friendId: String) {
        friendsCollection(authService.currentUserId).document(friendId).delete()

        friendsCollection(friendId).document(authService.currentUserId).delete()
    }

    override suspend fun sendRequest(friendRequest: FriendRequest, myRequest: FriendRequest) {
        sentCollection(authService.currentUserId)
            .document(friendRequest.userData.id).set(friendRequest.toEntity())

        usersCollection.document(friendRequest.userData.id)
            .collection("receivedRequests")
            .document(authService.currentUserId).set(myRequest.toEntity())
    }

    override suspend fun acceptRequest(friendRequest: FriendRequest) {
        receivedCollection(authService.currentUserId)
            .document(friendRequest.userData.id).delete()

        usersCollection.document(friendRequest.userData.id)
            .collection("sentRequests")
            .document(authService.currentUserId).delete()

        friendsCollection(authService.currentUserId).document(friendRequest.userData.id).set(friendRequest.toFriendEntity())

        val myself = FriendEntity(
            userData = UserData(
                id = authService.currentUserId,
                username = authService.currentUserName,
                name = authService.currentUserName,
                imageUrl = authService.currentUserImage
            )
        )
        usersCollection.document(friendRequest.userData.id)
            .collection("friends").document(myself.userData.id).set(myself)
    }

    override suspend fun declineRequest(friendRequest: FriendRequest) {
        receivedCollection(authService.currentUserId)
            .document(friendRequest.userData.id).delete()

        usersCollection.document(friendRequest.userData.id)
            .collection("sentRequests")
            .document(authService.currentUserId).delete()
    }

    override suspend fun cancelRequest(friendRequest: FriendRequest) {
        sentCollection(authService.currentUserId)
            .document(friendRequest.userData.id).delete()

        usersCollection.document(friendRequest.userData.id)
            .collection("receivedRequests")
            .document(authService.currentUserId).delete()
    }
}
