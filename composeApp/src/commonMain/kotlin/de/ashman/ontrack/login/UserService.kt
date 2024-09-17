package de.ashman.ontrack.login

import de.ashman.ontrack.login.model.UserDto
import de.ashman.ontrack.media.movie.model.entity.MovieEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database

class UserService {
    private val database = Firebase.database
    private val userRef = "users/${getUserId()}"

    suspend fun saveUser(user: UserDto) {
        database.reference(userRef).setValue(user)
    }

    suspend fun updateUserMovie(movie: MovieEntity) {
        val userRef = database.reference("$userRef/movies/${movie.id}")
        userRef.setValue(movie)
    }

    // TODO anders
    private fun getUserId(): String? = Firebase.auth.currentUser?.uid
}