package de.ashman.ontrack.media.book.model.dto

import kotlinx.serialization.Serializable

// TODO
@Serializable
data class BookWorksResponseDto(
    val title: String? = null,
    val description: String? = null,
)