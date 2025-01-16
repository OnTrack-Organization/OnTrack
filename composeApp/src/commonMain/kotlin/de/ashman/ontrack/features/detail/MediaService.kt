package de.ashman.ontrack.features.detail

import co.touchlab.kermit.Logger
import de.ashman.ontrack.entity.MediaEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database

class MediaService {
    suspend fun saveMediaEntity(mediaEntity: MediaEntity) {
        try {
            val mediaRef = Firebase.database.reference("users/${Firebase.auth.currentUser?.uid}/media/${mediaEntity.id}")
            mediaRef.setValue(mediaEntity)

            Logger.i { "MediaEntity saved successfully: $mediaEntity" }
        } catch (e: Exception) {
            Logger.e { "Error saving MediaEntity: ${e.message}" }
        }
    }
}