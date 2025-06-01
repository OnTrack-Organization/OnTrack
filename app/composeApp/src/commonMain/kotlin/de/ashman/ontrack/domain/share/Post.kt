package de.ashman.ontrack.domain.share

import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User

data class Post(
    val id: String,
    val user: User,
    val tracking: Tracking,
    val review: Review?,
    val likes: List<Like>,
    val comments: List<Comment>,
    val likedByCurrentUser: Boolean,
    val likeCount: Int,
    val commentCount: Int,
)
