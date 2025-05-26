package de.ashman.ontrack.feature.share.controller.dto

data class CreateCommentDto(
    val mentionedUsers: List<String>,
    val message: String
)
