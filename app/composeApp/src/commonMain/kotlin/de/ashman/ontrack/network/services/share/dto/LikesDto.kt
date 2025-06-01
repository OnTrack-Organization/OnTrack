package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.Likes
import kotlinx.serialization.Serializable

@Serializable
data class LikesDto(
    val likes: Page<LikeDto>,
    val likeCount: Int,
)

fun LikesDto.toDomain() = Likes(
    likes = likes.content.map { it.toDomain() },
    likeCount = likeCount,
)