package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.FriendEntity
import de.ashman.ontrack.db.entity.FriendRequestEntity
import de.ashman.ontrack.db.entity.UserEntity
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.toEntity
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface FriendService {
    suspend fun searchForNewFriend(query: String): List<FriendEntity>
    suspend fun getFriends(): List<FriendEntity>
    suspend fun getReceivedRequests(): List<FriendRequestEntity>
    suspend fun getSentRequests(): List<FriendRequestEntity>
    suspend fun removeFriend(friend: Friend)
    suspend fun sendRequest(otherRequest: FriendRequest, myRequest: FriendRequest)
    suspend fun acceptRequest(friendRequest: FriendRequest)
    suspend fun denyRequest(friendRequest: FriendRequest)
    suspend fun cancelRequest(friendId: String, friendRequest: FriendRequest)
}

class FriendServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FriendService {
    private val userCollection = firestore.collection("users")

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
}