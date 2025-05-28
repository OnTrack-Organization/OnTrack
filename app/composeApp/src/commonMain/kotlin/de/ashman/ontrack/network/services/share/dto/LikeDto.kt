package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class LikeDto(
    val id: String,
    val postId: String,
    val user: UserDto,
    val timestamp: Long
)

fun LikeDto.toDomain() = Like(
    id = id,
    postId = postId,
    user = user.toDomain(),
    timestamp = timestamp
)