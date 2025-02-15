package de.ashman.ontrack.authentication

import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthService {
    val currentUserId: String
    val currentUserImage: String
    val currentUserName: String

    suspend fun signUpUser(user: UserEntity)
    suspend fun deleteUser(userId: String)
    suspend fun getUserFlow(userId: String): Flow<UserEntity?>
}

class AuthServiceImpl(
    firestore: FirebaseFirestore,
) : AuthService {
    private val userCollection = firestore.collection("users")

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override val currentUserImage: String
        get() = Firebase.auth.currentUser?.photoURL.orEmpty()

    override val currentUserName: String
        get() = Firebase.auth.currentUser?.displayName.orEmpty()

    override suspend fun signUpUser(user: UserEntity) {
        return try {
            val document = userCollection.document(user.id).get()
            if (!document.exists) {
                userCollection
                    .document(user.id)
                    .set(user)
            } else {
                Logger.w("User with ID ${user.id} already exists.")
            }
        } catch (e: Exception) {
            Logger.e("Error during sign-up: ${e.message}")
        }
    }

    override suspend fun deleteUser(userId: String) {
        userCollection
            .document(userId)
            .delete()
    }

    override suspend fun getUserFlow(userId: String): Flow<UserEntity?> {
        return userCollection.document(userId).snapshots.map { documentSnapshot ->
            try {
                documentSnapshot.data<UserEntity>()
            } catch (e: Exception) {
                Logger.e { "Error parsing user document: ${e.message}" }
                null
            }
        }
    }
}