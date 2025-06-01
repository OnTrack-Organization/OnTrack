package de.ashman.ontrack.feature.notification.controller.dto

import de.ashman.ontrack.feature.share.domain.Comment
import java.util.*

data class SimpleCommentDto(
    val id: UUID,
    val postId: UUID,
    val message: String,
)

fun Comment.toSimpleDto() = SimpleCommentDto(
    id = id,
    postId = post.id,
    message = message,
)