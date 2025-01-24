package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.entity.MediaEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

interface MediaService {
    suspend fun saveMedia(media: MediaEntity)
    suspend fun removeMedia(id: String)
    suspend fun getAllUserMedia(): List<MediaEntity>
    suspend fun getUserMediaById(id: String): MediaEntity?
}

class MediaServiceImpl : MediaService {
    override suspend fun saveMedia(media: MediaEntity) {
        try {
            val mediaRef = Firebase.firestore
                .collection("users")
                .document(Firebase.auth.currentUser?.uid.orEmpty())
                .collection("media")
                .document(media.id)

            mediaRef.set(media)

            Logger.i { "Media saved successfully: $media" }
        } catch (e: Exception) {
            Logger.e { "Error saving media: ${e.message}" }
        }
    }

    override suspend fun removeMedia(id: String) {
        try {
            val mediaRef = Firebase.firestore
                .collection("users")
                .document(Firebase.auth.currentUser?.uid.orEmpty())
                .collection("media")
                .document(id)

            mediaRef.delete()

            Logger.i { "Media with ID $id deleted successfully." }
        } catch (e: Exception) {
            Logger.e { "Error deleting media with ID $id: ${e.message}" }
        }
    }

    override suspend fun getAllUserMedia(): List<MediaEntity> {
        return try {
            val mediaRef = Firebase.firestore
                .collection("users")
                .document(Firebase.auth.currentUser?.uid.orEmpty())
                .collection("media")

            val mediaResponse = mediaRef.get()

            Logger.i { "Successfully fetched all use media" }

            mediaResponse.documents.mapNotNull { document ->
                document.data()
            }
        } catch (e: Exception) {
            Logger.e { "Error fetching all user media: ${e.message}" }
            emptyList()
        }
    }

    override suspend fun getUserMediaById(id: String): MediaEntity? {
        return try {
            val mediaRef = Firebase.firestore
                .collection("users")
                .document(Firebase.auth.currentUser?.uid.orEmpty())
                .collection("media")
                .document(id)

            val mediaSnapshot = mediaRef.get()

            if (mediaSnapshot.exists) {
                mediaSnapshot.data()
            } else {
                Logger.i { "No media found with ID: $id" }
                null
            }
        } catch (e: Exception) {
            Logger.e { "Error fetching media with ID $id: ${e.message}" }
            null
        }
    }
}