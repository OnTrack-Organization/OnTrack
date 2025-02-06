package de.ashman.ontrack.authentication

import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthService {
    val currentUserId: String

    suspend fun signUpUser(user: UserEntity)
    suspend fun deleteUser(userId: String)
    suspend fun getUserFlow(userId: String): Flow<UserEntity?>
}

class AuthServiceImpl : AuthService {
    private val userRef = "users"

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override suspend fun signUpUser(user: UserEntity) {
        Firebase.firestore
            .collection(userRef)
            .document(user.id)
            .set(user)
    }

    override suspend fun deleteUser(userId: String) {
        Firebase.firestore
            .collection(userRef)
            .document(userId)
            .delete()
    }

    override suspend fun getUserFlow(userId: String): Flow<UserEntity?> {
        val userRef = Firebase.firestore
            .collection(userRef)
            .document(userId)

        return userRef.snapshots.map { documentSnapshot ->
            try {
                documentSnapshot.data<UserEntity>()
            } catch (e: Exception) {
                Logger.e { "Error parsing user document: ${e.message}" }
                null
            }
        }
    }
}