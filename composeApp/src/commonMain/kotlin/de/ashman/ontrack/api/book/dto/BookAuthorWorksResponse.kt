package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookAuthorWorksResponse(
    val entries: List<BookWorksResponse> = emptyList(),
    val size: Int? = null,
    val links: AuthorLinks? = null,
)

@Serializable
data class AuthorLinks(
    val self: String? = null,
    val author: String? = null,
    val next: String? = null,
)