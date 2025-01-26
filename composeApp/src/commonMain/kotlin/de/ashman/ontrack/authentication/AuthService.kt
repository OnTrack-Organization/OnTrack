package de.ashman.ontrack.authentication

import de.ashman.ontrack.authentication.user.UserEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface AuthService {
    suspend fun signUpUser(user: UserEntity)
    suspend fun deleteUser(userId: String)
}

class AuthServiceImpl : AuthService {
    private val userRef = "users"

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
}