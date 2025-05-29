package de.ashman.ontrack.feature.share.controller.dto

import de.ashman.ontrack.feature.review.controller.dto.ReviewDto
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import java.util.*

data class PostDto(
    val id: UUID,
    val user: UserDto,
    val tracking: TrackingDto,
    val review: ReviewDto?,
    val likes: List<LikeDto>,
    val comments: List<CommentDto>,
    val likedByCurrentUser: Boolean,
    val likeCount: Int,
    val commentCount: Int,
)
