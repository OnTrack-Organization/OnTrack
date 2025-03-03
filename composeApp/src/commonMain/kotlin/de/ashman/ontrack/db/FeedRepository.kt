package de.ashman.ontrack.db

import de.ashman.ontrack.domain.feed.Comment
import de.ashman.ontrack.domain.feed.Like
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface FeedRepository {
    suspend fun getTrackingFeed(lastTimestamp: Long?, limit: Int): Flow<List<Tracking>>
    suspend fun likeTracking(friendId: String, trackingId: String, like: Like)
    suspend fun unlikeTracking(friendId: String, trackingId: String, like: Like)
    suspend fun addComment(friendId: String, trackingId: String, comment: Comment)
    suspend fun removeComment(friendId: String, trackingId: String, comment: Comment)
}

class FeedRepositoryImpl(
    firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : FeedRepository {
    private val userCollection = firestore.collection("users")
    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getTrackingFeed(lastTimestamp: Long?, limit: Int): Flow<List<Tracking>> {
        val currentUserId = authRepository.currentUserId

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
                    .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>().toDomain() } }
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

    override suspend fun likeTracking(friendId: String, trackingId: String, like: Like) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayUnion(like.toEntity())
            )
    }

    override suspend fun unlikeTracking(friendId: String, trackingId: String, like: Like) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayRemove(like.toEntity())
            )
    }

    override suspend fun addComment(friendId: String, trackingId: String, comment: Comment) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayUnion(comment.toEntity())
            )
    }

    override suspend fun removeComment(userId: String, trackingId: String, comment: Comment) {
        userTrackingCollection(userId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayRemove(comment.toEntity())
            )
    }
}