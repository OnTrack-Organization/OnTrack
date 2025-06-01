package de.ashman.ontrack.network.services.share.dto

import de.ashman.ontrack.domain.share.Comments
import kotlinx.serialization.Serializable

@Serializable
data class CommentsDto(
    val comments: Page<CommentDto>,
    val commentCount: Int,
)

fun CommentsDto.toDomain() = Comments(
    comments = comments.content.map { it.toDomain() },
    commentCount = commentCount,
)