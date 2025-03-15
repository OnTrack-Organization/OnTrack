package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookSearchResponse(
    @SerialName("docs")
    val books: List<BookDto> = emptyList(),

    val numFound: Int,
    val numFoundExact: Boolean? = null,
    val offset: Int?,
    @SerialName("q")
    val query: String,
    val start: Int,
)