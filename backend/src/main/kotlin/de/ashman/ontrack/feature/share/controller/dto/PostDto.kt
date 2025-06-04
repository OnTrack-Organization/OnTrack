package de.ashman.ontrack.feature.share.controller.dto

import de.ashman.ontrack.feature.review.controller.dto.ReviewDto
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
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

fun Post.toDto(
    likes: List<LikeDto>,
    comments: List<CommentDto>,
    likedByCurrentUser: Boolean,
    likeCount: Int,
    commentCount: Int,
) = PostDto(
    id = id,
    user = user.toDto(),
    tracking = tracking.toDto(),
    review = review?.toDto(),
    likes = likes,
    comments = comments,
    likedByCurrentUser = likedByCurrentUser,
    likeCount = likeCount,
    commentCount = commentCount,
)