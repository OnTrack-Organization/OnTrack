package de.ashman.ontrack.domain.share

import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.domain.user.User

data class SimplePost(
    val id: String,
    val user: User,
    val tracking: NewTracking,
    val review: Review?,
    val likePreview: List<Like>,
    val commentPreview: List<Comment>,
    val likedByCurrentUser: Boolean,
    val likeCount: Int,
    val commentCount: Int
)

fun SimplePost.toPost() = Post(
    id = id,
    user = user,
    tracking = tracking,
    review = review,
    likes = likePreview,
    comments = commentPreview,
    likedByCurrentUser = likedByCurrentUser,
)