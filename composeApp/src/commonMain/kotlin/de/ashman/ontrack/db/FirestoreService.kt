package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.FriendEntity
import de.ashman.ontrack.db.entity.FriendRequestEntity
import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingLikeEntity
import de.ashman.ontrack.db.entity.UserEntity
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import kotlinx.coroutines.flow.Flow

// TODO einzelne services, kein ein großer
interface FirestoreService {
    // USER
    suspend fun getUserById(userId: String): UserEntity?
    suspend fun searchForNewFriend(query: String): List<FriendEntity>
    suspend fun getFriends(): List<FriendEntity>
    suspend fun getReceivedRequests(): List<FriendRequestEntity>
    suspend fun getSentRequests(): List<FriendRequestEntity>
    suspend fun removeFriend(friend: Friend)
    suspend fun sendRequest(otherRequest: FriendRequest, myRequest: FriendRequest)
    suspend fun acceptRequest(friendRequest: FriendRequest)
    suspend fun denyRequest(friendRequest: FriendRequest)
    suspend fun cancelRequest(friendId: String, friendRequest: FriendRequest)

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
