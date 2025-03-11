package de.ashman.ontrack.repository.firestore

import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.globalrating.RatingEntity
import de.ashman.ontrack.entity.globalrating.RatingStatsEntity
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.toEntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import de.ashman.ontrack.repository.CurrentUserRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface TrackingRepository {
    suspend fun saveTracking(tracking: Tracking)
    suspend fun removeTracking(trackingId: String, ratingId: String)

    suspend fun fetchTrackingById(userId: String, trackingId: String): Tracking?

    fun observeTrackings(userId: String): Flow<List<Tracking>>
    fun observeTracking(trackingId: String): Flow<Tracking?>

    suspend fun observeFriendTrackingsForMedia(mediaId: String): Flow<List<Tracking>>

    fun observeRatingStats(id: String): Flow<RatingStats?>
}

class TrackingRepositoryImpl(
    firestore: FirebaseFirestore,
    private val currentUserRepository: CurrentUserRepository,
) : TrackingRepository {
    private val userCollection = firestore.collection("users")
    private fun trackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    private val ratingsCollection = firestore.collection("ratings")
    private val ratingStatsCollection = firestore.collection("ratingStats")

    override suspend fun saveTracking(tracking: Tracking) {
        val trackingRef = trackingCollection(currentUserRepository.currentUserId).document(tracking.id)
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
            saveRating(id = "${tracking.mediaType}_${tracking.mediaId}", rating = it)
        }
    }

    override suspend fun removeTracking(trackingId: String, ratingId: String) {
        trackingCollection(currentUserRepository.currentUserId)
            .document(trackingId)
            .delete()

        // Remove rating as well
        removeRating(ratingId)
    }

    override suspend fun fetchTrackingById(userId: String, trackingId: String): Tracking? {
        val trackingRef = trackingCollection(userId)
            .document(trackingId)

        val snapshot = trackingRef.get()

        return if (!snapshot.exists) {
            Logger.i("Tracking not found for userId: $userId and trackingId: $trackingId")
            null
        } else {
            Logger.d("Tracking fetched for userId: $userId and trackingId: $trackingId")
            snapshot.data<TrackingEntity>().toDomain()
        }
    }

    override fun observeTracking(trackingId: String): Flow<Tracking?> {
        return trackingCollection(currentUserRepository.currentUserId)
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

    override fun observeTrackings(userId: String): Flow<List<Tracking>> {
        return trackingCollection(userId)
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>().toDomain() } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun observeFriendTrackingsForMedia(mediaId: String): Flow<List<Tracking>> {
        val currentUserId = currentUserRepository.currentUserId

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

    private suspend fun saveRating(id: String, rating: Double) {
        val userId = currentUserRepository.currentUserId
        val ratingRef = ratingsCollection
            .document(id)
            .collection("userRatings")
            .document(userId)

        ratingRef.set(
            RatingEntity(userId = userId, rating = rating)
        )

        // Now, update the rating stats
        updateRatingStats(id)
    }

    private suspend fun updateRatingStats(id: String) {
        val ratingsRef = ratingsCollection.document(id).collection("userRatings")
        val ratings = ratingsRef.get().documents.map { it.data<RatingEntity>() }

        val ratingCount = ratings.size
        val averageRating = if (ratingCount > 0) ratings.map { it.rating }.average() else 0.0

        val statsRef = ratingStatsCollection.document(id)
        statsRef.set(
            RatingStatsEntity(
                averageRating = averageRating,
                ratingCount = ratingCount
            )
        )
    }

    private suspend fun removeRating(id: String) {
        val userId = currentUserRepository.currentUserId
        val ratingDocRef = ratingsCollection
            .document(id)
            .collection("userRatings")
            .document(userId)

        ratingDocRef.delete()

        updateRatingStats(id)
    }

    override fun observeRatingStats(id: String): Flow<RatingStats?> {
        return ratingStatsCollection
            .document(id)
            .snapshots
            .map { snapshot ->
                if (snapshot.exists) {
                    snapshot.data<RatingStatsEntity>().toDomain()
                } else {
                    null
                }
            }
    }
}
