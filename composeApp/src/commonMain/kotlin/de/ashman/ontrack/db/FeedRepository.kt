package de.ashman.ontrack.db

import de.ashman.ontrack.domain.tracking.Comment
import de.ashman.ontrack.domain.tracking.Like
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface FeedRepository {
    suspend fun likeTracking(friendId: String, trackingId: String, like: Like)
    suspend fun unlikeTracking(friendId: String, trackingId: String, likeId: String)
    suspend fun addComment(friendId: String, trackingId: String, comment: Comment)
    suspend fun removeComment(friendId: String, trackingId: String, commentId: String)
}

class FeedRepositoryImpl(
    firestore: FirebaseFirestore,
) : FeedRepository {
    private val userCollection = firestore.collection("users")

    private fun trackingCollection(userId: String) = userCollection.document(userId).collection("trackings")
    private fun likesCollection(userId: String, trackingId: String) = trackingCollection(userId).document(trackingId).collection("likes")
    private fun commentsCollection(userId: String, trackingId: String) = trackingCollection(userId).document(trackingId).collection("comments")

    override suspend fun likeTracking(friendId: String, trackingId: String, like: Like) {
        likesCollection(friendId, trackingId)
            .document(like.userData.id)
            .set(like.toEntity())
    }

    override suspend fun unlikeTracking(friendId: String, trackingId: String, likeId: String) {
        likesCollection(friendId, trackingId)
            .document(likeId)
            .delete()
    }

    override suspend fun addComment(friendId: String, trackingId: String, comment: Comment) {
        commentsCollection(friendId, trackingId)
            .document(comment.id)
            .set(comment.toEntity())
    }

    override suspend fun removeComment(friendId: String, trackingId: String, commentId: String) {
        commentsCollection(friendId, trackingId)
            .document(commentId)
            .delete()
    }
}
