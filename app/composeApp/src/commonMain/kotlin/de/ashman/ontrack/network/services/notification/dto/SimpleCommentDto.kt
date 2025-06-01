package de.ashman.ontrack.network.services.notification.dto

import de.ashman.ontrack.domain.notification.SimpleComment
import kotlinx.serialization.Serializable

@Serializable
data class SimpleCommentDto(
    val id: String,
    val postId: String,
    val message: String,
)

fun SimpleCommentDto.toDomain() = SimpleComment(
    id = id,
    postId = postId,
    message = message,
)