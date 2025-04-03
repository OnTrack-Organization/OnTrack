package de.ashman.ontrack.repository.firestore

import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface ShareRepository {
    suspend fun fetchPage(friendIds: List<String>, pageSize: Int = 10, lastTimestamp: Long? = null): List<Tracking>
    suspend fun likeTracking(friendId: String, trackingId: String, like: Like)
    suspend fun unlikeTracking(friendId: String, trackingId: String, like: Like)
    suspend fun addComment(friendId: String, trackingId: String, comment: Comment)
    suspend fun removeComment(friendId: String, trackingId: String, comment: Comment)
}

class ShareRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : ShareRepository {

    private val userCollection = firestore.collection("users")
    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    override suspend fun fetchPage(
        friendIds: List<String>,
        pageSize: Int,
        lastTimestamp: Long?
    ): List<Tracking> {
        if (friendIds.isEmpty()) return emptyList()

        var query = firestore
            .collectionGroup("trackings")
            .where { "userId" inArray friendIds }
            .orderBy("timestamp", Direction.DESCENDING)
            .limit(pageSize)

        if (lastTimestamp != null) {
            query = query.startAfter(lastTimestamp)
        }

        return query.get().documents.map {
            it.data<TrackingEntity>().toDomain()
        }
    }

    override suspend fun likeTracking(friendId: String, trackingId: String, like: Like) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayUnion(like.toEntity().toMap())
            )
    }

    override suspend fun unlikeTracking(friendId: String, trackingId: String, like: Like) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likes" to FieldValue.arrayRemove(like.toEntity().toMap())
            )
    }

    override suspend fun addComment(friendId: String, trackingId: String, comment: Comment) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayUnion(comment.toEntity().toMap())
            )
    }

    override suspend fun removeComment(userId: String, trackingId: String, comment: Comment) {
        userTrackingCollection(userId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayRemove(comment.toEntity().toMap())
            )
    }
}