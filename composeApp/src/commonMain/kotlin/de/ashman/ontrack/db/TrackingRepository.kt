package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.toEntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

interface TrackingRepository {
    suspend fun saveTracking(tracking: Tracking)
    suspend fun removeTracking(trackingId: String)

    fun fetchTrackings(userId: String): Flow<List<Tracking>>
    fun fetchTracking(trackingId: String): Flow<Tracking?>

    suspend fun fetchFriendTrackingsForMedia(mediaId: String): Flow<List<Tracking>>

    suspend fun saveRating(mediaId: String, mediaType: MediaType, rating: Double)
    suspend fun removeUserRating(mediaId: String, mediaType: MediaType, userId: String)

    fun fetchRatingStats(mediaId: String, mediaType: MediaType): Flow<RatingStats?>
}

class TrackingRepositoryImpl(
    firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : TrackingRepository {
    private val userCollection = firestore.collection("users")
    private fun trackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    private val ratingsCollection = firestore.collection("ratings")
    private val ratingStatsCollection = firestore.collection("ratingStats")

    override suspend fun saveTracking(tracking: Tracking) {
        val trackingRef = trackingCollection(authRepository.currentUserId).document(tracking.id)
        val snapshot = trackingRef.get()

        val existingTracking = snapshot.takeIf { it.exists }?.data<TrackingEntity>()
        val updatedHistory = existingTracking?.takeIf { it.status != tracking.status }
            ?.let { it.history + it.toEntryEntity() }
            ?: emptyList()

        trackingRef.set(
            tracking.toEntity().copy(history = updatedHistory),
            merge = true
        )

        // If tracking has a rating, save it
        tracking.rating?.let {
            saveRating(mediaId = tracking.mediaId, mediaType = tracking.mediaType, rating = it)
        }
    }

    override suspend fun removeTracking(trackingId: String) {
        trackingCollection(authRepository.currentUserId)
            .document(trackingId)
            .delete()
    }

    override fun fetchTracking(trackingId: String): Flow<Tracking?> {
        return trackingCollection(authRepository.currentUserId)
            .document(trackingId)
            .snapshots
            .map { snapshot ->
                if (!snapshot.exists) {
                    Logger.i("Document does not exist for id: $trackingId")
                    null
                } else {
                    Logger.d("Document fetched for id: $trackingId")
                    snapshot.data<TrackingEntity>().toDomain()
                }
            }
    }

    override fun fetchTrackings(userId: String): Flow<List<Tracking>> {
        return trackingCollection(userId)
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>().toDomain() } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun fetchFriendTrackingsForMedia(mediaId: String): Flow<List<Tracking>> {
        val currentUserId = authRepository.currentUserId

        return userCollection.document(currentUserId)
            .collection("friends")
            .snapshots
            .map { it.documents.map { doc -> doc.id } }
            .flatMapLatest { friends ->
                friends
                    .map { friendId ->
                        trackingCollection(friendId)
                            .where { "mediaId" equalTo mediaId }
                            .snapshots
                            .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
                    }
                    .let { flows ->
                        if (flows.isEmpty()) flowOf(emptyList())
                        else combine(flows) { trackingLists ->
                            trackingLists.toList().flatten()
                                .groupBy { it.userId }
                                .mapNotNull { (_, trackings) -> trackings.maxByOrNull { it.timestamp }?.toDomain() }
                                .sortedByDescending { it.timestamp }
                        }
                    }
            }
    }

    override suspend fun saveRating(mediaId: String, mediaType: MediaType, rating: Double) {
        val userId = authRepository.currentUserId
        val ratingRef = ratingsCollection
            .document(mediaType.name)
            .collection(mediaId)
            .document(userId)

        ratingRef.set(
            data = RatingEntity(userId = userId, rating = rating)
        )

        updateRatingStats(mediaId, mediaType)
    }

    override suspend fun removeUserRating(mediaId: String, mediaType: MediaType, userId: String) {
        val ratingDocRef = ratingsCollection
            .document(mediaType.name)
            .collection(mediaId)
            .document(userId)

        ratingDocRef.delete()

        updateRatingStats(mediaId, mediaType)
    }

    override fun fetchRatingStats(mediaId: String, mediaType: MediaType): Flow<RatingStats?> {
        return ratingStatsCollection.document(mediaType.name)
            .collection(mediaId)
            .snapshots
            .map { snapshot ->
                // If there are no documents, return null
                if (snapshot.documents.isEmpty()) {
                    Logger.w("No RatingStats document found for mediaId: $mediaId of type $mediaType")
                    null
                } else {
                    // Fetch the first document in the snapshot
                    val document = snapshot.documents.firstOrNull()

                    // If the document exists, convert its data to RatingStatsEntity and then to RatingStats
                    document?.let {
                        try {
                            it.data<RatingStatsEntity>().toDomain()
                        } catch (e: Exception) {
                            Logger.e("Error decoding RatingStatsEntity: ${e.message}")
                            null
                        }
                    }
                }
            }
    }

    private suspend fun updateRatingStats(mediaId: String, mediaType: MediaType) {
        val ratingsSnapshot = ratingsCollection.document(mediaType.name)
            .collection(mediaId)
            .get()

        val ratings = ratingsSnapshot.documents.map { it.data<RatingEntity>() }
        val count = ratings.size
        val avg = if (count > 0) ratings.map { it.rating }.average() else 0.0

        ratingStatsCollection.document(mediaType.name)
            .collection(mediaId)
            .document("stats")
            .set(
                RatingStatsEntity(
                    averageRating = avg,
                    ratingCount = count
                )
            )
    }
}

@Serializable
data class RatingStats(
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0
)

fun RatingStatsEntity.toDomain() = RatingStats(
    averageRating = averageRating,
    ratingCount = ratingCount
)

@Serializable
data class RatingEntity(
    val userId: String,
    val rating: Double
)

@Serializable
data class RatingStatsEntity(
    val averageRating: Double,
    val ratingCount: Int,
)
