package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.MediaEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.UserEntity
import de.ashman.ontrack.domain.MediaType
import dev.gitlive.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FirestoreService {
    private val mediaCollection = firestore.collection("media")
    private val userCollection = firestore.collection("users")

    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    // MEDIA
    override suspend fun saveMedia(media: MediaEntity) {
        mediaCollection.document(media.id).set(data = media, merge = true)
    }

    override suspend fun getMediaById(id: String): MediaEntity? {
        return mediaCollection.document(id).get().data()
    }

    override suspend fun getRandomCoverForEveryMediaType(): List<String> {
        val coverUrls = mutableListOf<String>()

        for (mediaType in MediaType.entries) {
            val query = mediaCollection
                .where { "type" equalTo mediaType.name }
                .limit(1)

            val snapshot: QuerySnapshot = query.get()

            snapshot.documents.firstOrNull()?.let {
                val coverUrl = it.data<MediaEntity>().coverUrl
                coverUrl?.let { url -> coverUrls.add(url) }
            }
        }

        return coverUrls
    }

    // USER
    override suspend fun getUserById(userId: String): UserEntity? {
        return userCollection.document(userId).get().data()
    }

    override suspend fun addFriend(friendId: String) {
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayUnion(friendId)
        )
    }

    override suspend fun removeFriend(friendId: String) {
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayRemove(friendId)
        )
    }

    // TRACKING
    override suspend fun saveTracking(tracking: TrackingEntity) {
        val trackingRef = userTrackingCollection(authService.currentUserId)
            .document(tracking.id)
        trackingRef.set(tracking)

        // Update the global media rating
        //incrementRating(tracking)
    }

    override suspend fun deleteTrackingsByMediaId(mediaId: String) {
        val documents = userTrackingCollection(authService.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .get()
            .documents

        documents.forEach {
            it.reference.delete()
        }
    }

    override fun consumeLatestUserTrackings(userId: String): Flow<List<TrackingEntity>> {
        return userTrackingCollection(userId)
            .orderBy("updatedAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data<TrackingEntity>() }
                    .groupBy { it.mediaId }
                    .map { it.value.first() }
            }
    }

    override fun consumeLatestUserTracking(mediaId: String): Flow<TrackingEntity?> {
        return userTrackingCollection(authService.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .orderBy("updatedAt", Direction.DESCENDING)
            .limit(1)
            .snapshots
            .map { it.documents.firstOrNull()?.data() }
    }

    // TODO add a working rating update
    /*private suspend fun incrementRating(tracking: TrackingEntity) {
        val userTrackings = userTrackingCollection(currentUserId)
            .where { "mediaId" equalTo tracking.mediaId }
            .get()
            .documents
            .map { it.data<TrackingEntity>() }

        val mediaRef = mediaCollection.document(tracking.mediaId)
        val media = mediaRef.get().data<MediaEntity>()

        // Check if this is the first tracking for this media by the user
        val isFirstTracking = userTrackings.size == 1

        // Check if this is the first rating for this media by the user
        val hasExistingRating = userTrackings.any { it.rating != null && it.id != tracking.id }
        val isFirstRating = tracking.rating != null && !hasExistingRating

        val newTrackedCount = if (isFirstTracking) media.trackedCount + 1 else media.trackedCount
        val newRatingCount = if (isFirstRating) media.ratingCount + 1 else media.ratingCount

        val newAverageRating = if (tracking.rating != null) {
            val totalRating = (media.averageRating * media.ratingCount) + tracking.rating
            totalRating / newRatingCount
        } else {
            media.averageRating
        }

        mediaRef.update(
            "trackedCount" to newTrackedCount,
            "ratingCount" to newRatingCount,
            "averageRating" to newAverageRating
        )
    }

    private suspend fun decrementRating(mediaId: String, removedRating: Int?) {
        val mediaRef = mediaCollection.document(mediaId)
        val media = mediaRef.get().data<MediaEntity>()

        val newTrackedCount = (media.trackedCount - 1).coerceAtLeast(0)
        val newRatingCount = (media.ratingCount - 1).coerceAtLeast(0)
        val newAverageRating = if (removedRating != null && media.ratingCount > 1) {
            ((media.averageRating * media.ratingCount) - removedRating) / newRatingCount
        } else {
            0.0
        }

        mediaRef.update(
            "trackedCount" to newTrackedCount,
            "ratingCount" to newRatingCount,
            "averageRating" to newAverageRating,
        )
    }*/

}
