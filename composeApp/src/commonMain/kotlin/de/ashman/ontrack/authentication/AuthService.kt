package de.ashman.ontrack.authentication

import de.ashman.ontrack.user.UserEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class AuthService {
    private val userRef = "users"

    suspend fun signUpUser(user: UserEntity) {
        Firebase.firestore.collection(userRef).document(user.id).set(user)
    }

    suspend fun deleteUser(userId: String) {
        Firebase.firestore.collection(userRef).document(userId).delete()
    }
}