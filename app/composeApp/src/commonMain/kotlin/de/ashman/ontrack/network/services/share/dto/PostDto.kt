package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.network.services.review.dto.ReviewDto
import de.ashman.ontrack.network.services.review.dto.toDomain
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import de.ashman.ontrack.network.services.tracking.dto.TrackingDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: String,
    val user: UserDto,
    val tracking: TrackingDto,
    val review: ReviewDto?,
    val likes: List<LikeDto>,
    val comments: List<CommentDto>,
    val likedByCurrentUser: Boolean,
    val likeCount: Int,
    val commentCount: Int,
)

fun PostDto.toDomain() = Post(
    id = id,
    user = user.toDomain(),
    tracking = tracking.toDomain(),
    review = review?.toDomain(),
    likes = likes.map { it.toDomain() },
    comments = comments.map { it.toDomain() },
    likedByCurrentUser = likedByCurrentUser,
    likeCount = likeCount,
    commentCount = commentCount,
)