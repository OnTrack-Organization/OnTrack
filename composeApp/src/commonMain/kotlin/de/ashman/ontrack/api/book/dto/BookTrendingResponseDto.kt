package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookTrendingResponseDto(
    @SerialName("works")
    val books: List<BookDto> = emptyList(),

    val days: Int,
    val hours: Int,
    val query: String,
)