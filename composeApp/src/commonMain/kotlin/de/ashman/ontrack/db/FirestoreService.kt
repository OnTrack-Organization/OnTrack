package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.MediaEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    // MEDIA
    suspend fun saveMedia(media: MediaEntity)
    suspend fun getMediaById(id: String): MediaEntity?

    // USER
    suspend fun getUserById(userId: String): UserEntity?
    suspend fun addFriend(friendId: String)
    suspend fun removeFriend(friendId: String)

    // TRACKING
    suspend fun saveTracking(tracking: TrackingEntity)
    suspend fun deleteTrackingsByMediaId(mediaId: String)
    fun consumeLatestUserTrackings(): Flow<List<TrackingEntity>>
    fun consumeLatestUserTracking(mediaId: String): Flow<TrackingEntity?>
}
