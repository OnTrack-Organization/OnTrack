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

    suspend fun signUp(user: UserEntity)
    suspend fun signOut()
    suspend fun deleteUser()
    suspend fun consumeUser(userId: String): Flow<UserEntity?>
    suspend fun updateFcmToken(token: String)
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

    override suspend fun signUp(user: UserEntity) {
        return try {
            val document = userCollection.document(user.id).get()
            if (!document.exists) {
                userCollection
                    .document(user.id)
                    .set(user)

                Logger.d("User with ID ${user.id} successfully added.")
            } else {
                Logger.w("User with ID ${user.id} already exists.")
            }
        } catch (e: Exception) {
            Logger.e("Error during sign-up: ${e.message}")
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteUser() {
        userCollection
            .document(currentUserId)
            .delete()
    }

    override suspend fun consumeUser(userId: String): Flow<UserEntity?> {
        return userCollection.document(userId).snapshots.map { documentSnapshot ->
            try {
                documentSnapshot.data<UserEntity>()
            } catch (e: Exception) {
                Logger.e { "Error parsing user document: ${e.message}" }
                null
            }
        }
    }

    override suspend fun updateFcmToken(token: String) {
        userCollection
            .document(currentUserId)
            .set(
                data = mapOf("fcmToken" to token),
                merge = true
            )
    }
}