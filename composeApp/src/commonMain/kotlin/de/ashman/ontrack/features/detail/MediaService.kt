package de.ashman.ontrack.features.detail

import co.touchlab.kermit.Logger
import de.ashman.ontrack.user.UserEntity
import de.ashman.ontrack.util.TrackStatusEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database

class MediaService {
    suspend fun updateTrackStatus(mediaId: String, status: TrackStatusEntity) {
        try {
            val mediaRef = Firebase.database.reference("users/${Firebase.auth.currentUser?.uid}/media/$mediaId/trackStatus")

            mediaRef.setValue(status)

            Logger.i { "Track status updated successfully" }
        } catch (e: Exception) {
            Logger.e { "Error updating track status: ${e.message}" }
        }
    }
}