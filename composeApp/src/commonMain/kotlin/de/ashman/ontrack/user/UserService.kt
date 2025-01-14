package de.ashman.ontrack.user

import de.ashman.ontrack.user.model.UserDto
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.Videogame
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.first

class UserService {
    val database = Firebase.database
    val userRef = "users/${getUserId()}"

    suspend fun saveUser(user: UserDto) {
        database.reference(userRef).setValue(user)
    }

    suspend inline fun <reified T : Media> updateUserMedia(media: T) {
        val mediaRef = database.reference("$userRef/${media.mediaType}/${media.id}")
        mediaRef.setValue(media)
    }

    suspend inline fun <reified T : Media> getAllSavedMediaForType(mediaType: String): List<T> {
        val mediaRef = database.reference("$userRef/$mediaType").valueEvents.first()
        return mediaRef.children.map { child ->
            // TODO this is ASS, change
            when (mediaType) {
                MediaType.MOVIE.name -> child.value<Movie>() as T
                MediaType.SHOW.name -> child.value<Show>() as T
                MediaType.BOOK.name -> child.value<Book>() as T
                MediaType.VIDEOGAME.name -> child.value<Videogame>() as T
                MediaType.BOARDGAME.name -> child.value<Boardgame>() as T
                MediaType.ALBUM.name -> child.value<Album>() as T
                else -> throw IllegalArgumentException("Unknown media type")
            }
        }
    }

    private fun getUserId(): String? = Firebase.auth.currentUser?.uid
}
