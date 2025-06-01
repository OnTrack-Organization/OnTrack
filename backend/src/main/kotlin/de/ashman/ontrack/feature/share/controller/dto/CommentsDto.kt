package de.ashman.ontrack.feature.share.controller.dto

import org.springframework.data.domain.Page

data class CommentsDto(
    val comments: Page<CommentDto>,
    val commentCount: Int,
)
