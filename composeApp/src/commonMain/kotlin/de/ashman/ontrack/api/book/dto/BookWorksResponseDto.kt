package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookWorksResponseDto(
    @Serializable(with = DescriptionSerializer::class)
    val description: String? = null,
)
