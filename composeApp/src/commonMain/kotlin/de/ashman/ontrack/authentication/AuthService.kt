package de.ashman.ontrack.authentication

import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthService {
    val currentUserId: String
    val currentUserImage: String
    val currentUserName: String

    suspend fun createUser(user: UserEntity): Boolean
    suspend fun removeUser()
    suspend fun updateUser(user: UserEntity)

    suspend fun updateFcmToken(token: String)

    suspend fun signOut()

    suspend fun observeUser(userId: String): Flow<UserEntity?>
    suspend fun isUsernameTaken(username: String): Boolean
}

class AuthServiceImpl(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
) : AuthService {
    private val userCollection = firestore.collection("users")

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUserImage: String
        get() = auth.currentUser?.photoURL.orEmpty()

    override val currentUserName: String
        get() = auth.currentUser?.displayName.orEmpty()

    override suspend fun createUser(user: UserEntity): Boolean {
        val document = userCollection.document(user.id).get()
        if (!document.exists) {
            userCollection
                .document(user.id)
                .set(user)

            Logger.d("User with ID ${user.id} successfully added.")
            return true
        } else {
            Logger.w("User with ID ${user.id} already exists.")
            return false
        }
    }

    override suspend fun removeUser() {
        userCollection
            .document(currentUserId)
            .delete()
    }

    // TODO make this work
    override suspend fun updateFcmToken(token: String) {
        userCollection
            .document(currentUserId)
            .update("fcmToken" to token)
    }

    override suspend fun updateUser(user: UserEntity) {
        userCollection
            .document(currentUserId)
            .set(
                data = user,
                merge = true
            )
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun observeUser(userId: String): Flow<UserEntity?> {
        return userCollection.document(userId).snapshots.map { documentSnapshot ->
            try {
                documentSnapshot.data<UserEntity>()
            } catch (e: Exception) {
                Logger.e { "Error parsing user document: ${e.message}" }
                null
            }
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val querySnapshot = userCollection
                .where { "username" equalTo username }
                .get()

            !querySnapshot.documents.isEmpty()
        } catch (e: Exception) {
            Logger.e { "Error checking username availability: ${e.message}" }
            false
        }
    }
}