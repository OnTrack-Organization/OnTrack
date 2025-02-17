package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingLikeEntity
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface FeedService {
    suspend fun getTrackingFeed(lastTimestamp: Long?, limit: Int): Flow<List<TrackingEntity>>
    suspend fun likeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity)
    suspend fun unlikeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity)
    suspend fun addComment(friendId: String, trackingId: String, comment: TrackingCommentEntity)
    suspend fun deleteComment(friendId: String, trackingId: String, comment: TrackingCommentEntity)
}

class FeedServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FeedService {
    private val userCollection = firestore.collection("users")
    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getTrackingFeed(lastTimestamp: Long?, limit: Int): Flow<List<TrackingEntity>> {
        val currentUserId = authService.currentUserId

        val friendsFlow = userCollection.document(currentUserId)
            .collection("friends")
            .snapshots()
            .map { snapshot -> snapshot.documents.map { it.id } + currentUserId }

        return friendsFlow.flatMapLatest { friends ->
            val allFlows = friends.map { friendId ->
                var query = userTrackingCollection(friendId)
                    .orderBy("timestamp", Direction.DESCENDING)

                if (lastTimestamp != null) {
                    query = query.startAfter(lastTimestamp)
                }

                query.snapshots()
                    .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
            }

            if (allFlows.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(allFlows) { trackingLists ->
                    trackingLists
                        .toList()
                        .flatten()
                        .distinctBy { it.id }
                        .sortedByDescending { it.timestamp }
                        .take(limit)
                }
            }
        }
    }

    override suspend fun likeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayUnion(like.toMap())
            )
    }

    override suspend fun unlikeTracking(friendId: String, trackingId: String, like: TrackingLikeEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayRemove(like.toMap())
            )
    }

    override suspend fun addComment(friendId: String, trackingId: String, comment: TrackingCommentEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayUnion(comment.toMap())
            )
    }

    override suspend fun deleteComment(friendId: String, trackingId: String, comment: TrackingCommentEntity) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayRemove(comment.toMap())
            )
    }
}