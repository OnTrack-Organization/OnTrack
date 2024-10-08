package de.ashman.ontrack.login

import de.ashman.ontrack.login.model.UserDto
import de.ashman.ontrack.media.MediaEntity
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

    suspend inline fun <reified T : MediaEntity> updateUserMedia(media: T) {
        val mediaRef = database.reference("$userRef/${media.type}/${media.id}")
        mediaRef.setValue(media)
    }

    suspend inline fun <reified T : MediaEntity> getSavedMedia(mediaType: String): List<T> {
        val mediaRef = database.reference("$userRef/$mediaType").valueEvents.first()
        return mediaRef.children.map { it.value<T>() }
    }

    private fun getUserId(): String? = Firebase.auth.currentUser?.uid
}
