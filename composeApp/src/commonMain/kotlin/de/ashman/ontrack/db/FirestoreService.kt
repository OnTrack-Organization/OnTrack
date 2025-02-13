package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingLikeEntity
import de.ashman.ontrack.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    // USER
    suspend fun getUserById(userId: String): UserEntity?
    suspend fun addFriend(friendId: String)
    suspend fun removeFriend(friendId: String)

    // TRACKING
    suspend fun saveTracking(tracking: TrackingEntity)
    suspend fun deleteTracking(trackingId: String)
    fun fetchTrackings(userId: String): Flow<List<TrackingEntity>>
    fun fetchTracking(trackingId: String): Flow<TrackingEntity?>
    suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>>

    // FEED
    suspend fun getTrackingFeed(lastTimestamp: Long?, limit: Int): Flow<List<TrackingEntity>>
    suspend fun likeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity)
    suspend fun unlikeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity)
    suspend fun addComment(friendId: String, trackingId: String, comment: TrackingCommentEntity)
    suspend fun deleteComment(friendId: String, trackingId: String, comment: TrackingCommentEntity)
}
