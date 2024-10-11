package de.ashman.ontrack.login

import de.ashman.ontrack.login.model.UserDto
import de.ashman.ontrack.media.domain.Album
import de.ashman.ontrack.media.domain.BoardGame
import de.ashman.ontrack.media.domain.Book
import de.ashman.ontrack.media.domain.Media
import de.ashman.ontrack.media.domain.MediaType
import de.ashman.ontrack.media.domain.Movie
import de.ashman.ontrack.media.domain.Show
import de.ashman.ontrack.media.domain.VideoGame
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
        val mediaRef = database.reference("$userRef/${media.type}/${media.id}")
        mediaRef.setValue(media)
    }

    suspend inline fun <reified T : Media> getSavedMedia(mediaType: String): List<T> {
        val mediaRef = database.reference("$userRef/$mediaType").valueEvents.first()
        return mediaRef.children.map { child ->
            // TODO this is ASS, change
            when (mediaType) {
                MediaType.MOVIE.name -> child.value<Movie>() as T
                MediaType.SHOW.name -> child.value<Show>() as T
                MediaType.BOOK.name -> child.value<Book>() as T
                MediaType.VIDEOGAME.name -> child.value<VideoGame>() as T
                MediaType.BOARDGAME.name -> child.value<BoardGame>() as T
                MediaType.ALBUM.name -> child.value<Album>() as T
                else -> throw IllegalArgumentException("Unknown media type")
            }
        }
    }

    private fun getUserId(): String? = Firebase.auth.currentUser?.uid
}
