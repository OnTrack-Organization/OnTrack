package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.entity.MediaEntity
import de.ashman.ontrack.domain.RatingStats
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface MediaService {
    suspend fun saveMedia(media: MediaEntity)
    suspend fun removeMedia(id: String)
    fun getUserMediaListFlow(): Flow<List<MediaEntity>>
    fun getUserMediaFlow(id: String): Flow<MediaEntity?>
    fun getMediaRatingStatsFlow(mediaId: String): Flow<RatingStats>
}

class MediaServiceImpl : MediaService {
    private val userId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override suspend fun saveMedia(media: MediaEntity) {
        try {
            val mediaRef = Firebase.firestore
                .collection("users")
                .document(userId)
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
                .document(userId)
                .collection("media")
                .document(id)

            mediaRef.delete()

            Logger.i { "Media with ID $id deleted successfully." }
        } catch (e: Exception) {
            Logger.e { "Error deleting media with ID $id: ${e.message}" }
        }
    }

    override fun getUserMediaListFlow(): Flow<List<MediaEntity>> {
        if (userId.isEmpty()) {
            Logger.e { "User is not logged in. Unable to fetch media list." }
            return flowOf(emptyList())
        }

        val mediaRef = Firebase.firestore
            .collection("users")
            .document(userId)
            .collection("media")

        return mediaRef.snapshots.map { querySnapshot ->
            querySnapshot.documents.mapNotNull { documentSnapshot ->
                try {
                    documentSnapshot.data<MediaEntity>()
                } catch (e: Exception) {
                    Logger.e { "Error parsing media document: ${e.message}" }
                    null
                }
            }
        }
    }

    override fun getUserMediaFlow(id: String): Flow<MediaEntity?> {
        val mediaRef = Firebase.firestore
            .collection("users")
            .document(userId)
            .collection("media")
            .document(id)

        return mediaRef.snapshots.map { documentSnapshot ->
            try {
                if (documentSnapshot.exists) {
                    documentSnapshot.data<MediaEntity>()
                } else {
                    Logger.i { "No media found with ID: $id" }
                    null
                }
            } catch (e: Exception) {
                Logger.e { "Error parsing media document for ID $id: ${e.message}" }
                null
            }
        }
    }

    override fun getMediaRatingStatsFlow(id: String): Flow<RatingStats> {
        val ratingsRef = Firebase.firestore
            .collection("media")
            .document(id)
            .collection("ratings")

        return ratingsRef.snapshots.map { querySnapshot ->
            val ratings = querySnapshot.documents.map { doc ->
                doc.get<Double>("rating")
            }

            val ratingCount = ratings.size
            val averageRating = ratings.average()

            RatingStats(
                totalAppRatings = ratingCount,
                averageAppRating = averageRating,
            )
        }
    }

}