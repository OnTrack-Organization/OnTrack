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
    val postedByCurrentUser: Boolean,
    val deletable: Boolean,
    val timestamp: Long,
)

fun Comment.toDto(currentUserId: String, postOwnerId: String) = CommentDto(
    id = id,
    postId = post.id,
    user = user.toDto(),
    mentionedUsers = mentionedUsers.map { it.toDto() },
    message = message,
    // TODO
    likeCount = 0,
    postedByCurrentUser = user.id == currentUserId,
    deletable = user.id == currentUserId || postOwnerId == currentUserId,
    timestamp = createdAt.toEpochMilli(),
)