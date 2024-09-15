package de.ashman.ontrack.login

import de.ashman.ontrack.login.model.UserDto
import de.ashman.ontrack.media.movie.model.MovieDto
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database

class UserService {
    private val database = Firebase.database
    private val usersRef = "users"

    private var userId = ""

    suspend fun saveUser(user: UserDto) {
        database.reference("$usersRef/${user.id}").setValue(user)

        // TODO remove by using datastore
        userId = user.id
    }

    suspend fun updateUserMovie(movieDto: MovieDto) {
        val userRef = database.reference("$usersRef/$userId/movies/${movieDto.id}")
        userRef.setValue(movieDto)
    }
}