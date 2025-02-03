package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.MediaEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreServiceImpl(
    firestore: FirebaseFirestore
) : FirestoreService {

    private val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

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

    // USER
    override suspend fun getUserById(userId: String): UserEntity? {
        return userCollection.document(userId).get().data()
    }

    override suspend fun addFriend(friendId: String) {
        userCollection.document(currentUserId).update(
            "friends" to FieldValue.arrayUnion(friendId)
        )
    }

    override suspend fun removeFriend(friendId: String) {
        userCollection.document(currentUserId).update(
            "friends" to FieldValue.arrayRemove(friendId)
        )
    }

    // TRACKING
    override suspend fun saveTracking(tracking: TrackingEntity) {
        val trackingRef = userTrackingCollection(currentUserId).document(tracking.id)
        trackingRef.set(tracking)

        // Update the global media rating
        updateRating(tracking)
    }

    override suspend fun deleteTrackingsByMediaId(mediaId: String) {
        val documents = userTrackingCollection(currentUserId)
            .where { "mediaId" equalTo mediaId }
            .get()
            .documents

        documents.forEach {
            it.reference.delete()
        }
    }

    override fun consumeLatestUserTrackings(): Flow<List<TrackingEntity>> {
        return userTrackingCollection(currentUserId)
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
        return userTrackingCollection(currentUserId)
            .where { "mediaId" equalTo mediaId }
            .orderBy("updatedAt", Direction.DESCENDING)
            .limit(1)
            .snapshots
            .map { it.documents.firstOrNull()?.data() }
    }

    // TODO add a working rating update
    private suspend fun updateRating(tracking: TrackingEntity) {
        // Check if the user has already tracked this media
        val userTrackingDoc = userTrackingCollection(currentUserId)
            .where { "mediaId" equalTo tracking.mediaId }
            .get()

        val existingTracking = userTrackingDoc.documents.firstOrNull()?.data<TrackingEntity>()
        if (existingTracking != null && existingTracking.rating == tracking.rating) {
            // Rating hasn't changed, no need to update
            return
        }

        val mediaDoc = mediaCollection.document(tracking.mediaId).get()
        val media = mediaDoc.data<MediaEntity>()

        val newTrackedCount = media.trackedCount + if (tracking.rating != null) 1 else 0
        val newSum = media.averageRating * media.ratingCount.toDouble().plus(tracking.rating ?: 0)
        val newAverageRating = if (newTrackedCount > 0) newSum / newTrackedCount else 0.0

        val updatedMedia = media.copy(
            trackedCount = newTrackedCount,
            averageRating = newAverageRating,
        )

        mediaCollection.document(tracking.mediaId).set(updatedMedia, merge = true)
    }

}
