package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.SimplePost
import de.ashman.ontrack.network.services.review.dto.ReviewDto
import de.ashman.ontrack.network.services.review.dto.toDomain
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import de.ashman.ontrack.network.services.tracking.dto.TrackingDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class SimplePostDto(
    val id: String,
    val user: UserDto,
    val tracking: TrackingDto,
    val review: ReviewDto?,
    val likePreview: List<LikeDto>,
    val commentPreview: List<CommentDto>,
    val likedByCurrentUser: Boolean,
    val likeCount: Int,
    val commentCount: Int
)

fun SimplePostDto.toDomain() = SimplePost(
    id = id,
    user = user.toDomain(),
    tracking = tracking.toDomain(),
    review = review?.toDomain(),
    likePreview = likePreview.map { it.toDomain() },
    commentPreview = commentPreview.map { it.toDomain() },
    likedByCurrentUser = likedByCurrentUser,
    likeCount = likeCount,
    commentCount = commentCount,
)