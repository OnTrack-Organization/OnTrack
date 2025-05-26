package de.ashman.ontrack.feature.share.controller.dto

import de.ashman.ontrack.feature.share.domain.Like
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import java.util.*

data class LikeDto(
    val id: UUID,
    val postId: UUID,
    val user: UserDto,
    val timestamp: Long
)

fun Like.toDto() = LikeDto(
    id = id,
    postId = post.id,
    user = user.toDto(),
    timestamp = createdAt.toEpochMilli(),
)