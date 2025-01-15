package de.ashman.ontrack.authentication

import de.ashman.ontrack.user.UserEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database

class AuthRepository {
    private val userRef = "users"

    suspend fun signUpUser(user: UserEntity) {
        Firebase.database.reference("$userRef/${user.id}").setValue(user)
    }

    suspend fun deleteUser(userId: String) {
        Firebase.database.reference("$userRef/$userId").removeValue()
    }
}