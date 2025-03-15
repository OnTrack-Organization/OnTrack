package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookEditionsResponse(
    val entries: List<BookEditionsEntry>,
)

@Serializable
data class BookEditionsEntry(
    val numberOfPages: Int? = null,
    val publishDate: String? = null,
    val weight: String? = null,
)
