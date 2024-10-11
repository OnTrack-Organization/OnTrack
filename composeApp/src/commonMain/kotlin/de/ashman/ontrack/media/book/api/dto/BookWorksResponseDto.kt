package de.ashman.ontrack.media.book.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookWorksResponseDto(
    val description: DescriptionDto? = null,
)

@Serializable
data class DescriptionDto(
    val type: String,
    val value: String,
)