package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val postId: String,
    val user: UserDto,
    val mentionedUsers: List<UserDto>,
    val message: String,
    val postedByCurrentUser: Boolean,
    val deletable: Boolean,
    val timestamp: Long,
)

fun CommentDto.toDomain() = Comment(
    id = id,
    postId = postId,
    user = user.toDomain(),
    mentionedUsers = mentionedUsers.map { it.toDomain() },
    message = message,
    postedByCurrentUser = postedByCurrentUser,
    deletable = deletable,
    timestamp = timestamp,
)