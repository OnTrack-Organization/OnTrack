package de.ashman.ontrack.domain.share

import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.domain.user.User

data class Post(
    val id: String,
    val user: User,
    val tracking: NewTracking,
    val review: Review?,
    val likes: List<Like>,
    val comments: List<Comment>,
    val likedByCurrentUser: Boolean,
)
