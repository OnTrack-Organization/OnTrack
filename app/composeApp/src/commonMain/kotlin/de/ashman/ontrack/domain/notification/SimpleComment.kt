package de.ashman.ontrack.domain.notification


data class SimpleComment(
    val id: String,
    val postId: String,
    val message: String,
)
