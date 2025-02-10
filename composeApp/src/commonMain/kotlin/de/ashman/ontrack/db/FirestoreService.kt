package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    // USER
    suspend fun getUserById(userId: String): UserEntity?
    suspend fun addFriend(friendId: String)
    suspend fun removeFriend(friendId: String)

    // TRACKING
    suspend fun saveTracking(tracking: TrackingEntity)
    suspend fun deleteTrackingsByMediaId(mediaId: String)
    fun fetchTrackings(userId: String): Flow<List<TrackingEntity>>
    fun fetchTracking(mediaId: String): Flow<TrackingEntity?>

    // FEED
    suspend fun getTrackingFeed(): List<TrackingEntity>
    suspend fun likeTracking(friendId: String, trackingId: String)
    suspend fun unlikeTracking(friendId: String, trackingId: String)
    suspend fun addComment(friendId: String, trackingId: String, comment: String)
    suspend fun deleteComment(friendId: String, trackingId: String, commentId: String)
}
