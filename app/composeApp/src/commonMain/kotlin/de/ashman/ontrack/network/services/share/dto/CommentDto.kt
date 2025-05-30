package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val postId: String,
    val user: UserDto,
    val mentionedUsers: List<UserDto>,
    val message: String,
    val likeCount: Int,
    val postedByCurrentUser: Boolean,
    // TODO why not working bruh
    val deletable: Boolean,
    val timestamp: Long,
)

fun CommentDto.toDomain() = Comment(
    id = id,
    postId = postId,
    user = user.toDomain(),
    mentionedUsers = mentionedUsers.map { it.toDomain() },
    message = message,
    likeCount = likeCount,
    postedByCurrentUser = postedByCurrentUser,
    deletable = this@toDomain.deletable,
    timestamp = timestamp,
)