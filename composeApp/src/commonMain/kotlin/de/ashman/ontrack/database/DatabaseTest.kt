package de.ashman.ontrack.database

import de.ashman.ontrack.movie.model.Movie
import de.ashman.ontrack.movie.model.MovieDto
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class DatabaseTest() {
    private val db = Firebase.database

    init {
        CoroutineScope(Dispatchers.IO).launch {
            setUser(23)
            setUser(42)
            setReview()
            getUsers()
        }
    }

    suspend fun setUser(id: Int) {
        val ref = db.reference("users/$id")
        ref.setValue(MovieDto(id))
        println("SET")
    }

    suspend fun setReview() {
        val ref = db.reference("reviews")
        ref.setValue("Review")
    }

    suspend fun getUsers() {
        val ref = db.reference("users")
        ref.valueEvents.collect {
            println(it.value)
        }
    }
}