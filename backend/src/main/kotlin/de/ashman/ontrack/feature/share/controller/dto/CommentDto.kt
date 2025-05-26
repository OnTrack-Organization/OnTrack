package de.ashman.ontrack.feature.share.controller.dto

import de.ashman.ontrack.feature.share.domain.Comment
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import java.util.*

data class CommentDto(
    val id: UUID,
    val postId: UUID,
    val user: UserDto,
    val mentionedUsers: List<UserDto>,
    val message: String,
    val likeCount: Int,
    val timestamp: Long,
)

fun Comment.toDto() = CommentDto(
    id = id,
    postId = post.id,
    user = user.toDto(),
    mentionedUsers = mentionedUsers.map { it.toDto() },
    message = message,
    // TODO
    likeCount = 0,
    timestamp = createdAt.toEpochMilli(),
)