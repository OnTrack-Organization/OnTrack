package de.ashman.ontrack.login

import de.ashman.ontrack.login.model.UserDto
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database

class UserService() {
    private val database = Firebase.database
    private val usersRef = "users"

    suspend fun saveUser(user: UserDto) {
        database.reference("$usersRef/${user.id}").setValue(user)
    }
}