package de.ashman.ontrack.api.book.dto

import de.ashman.ontrack.api.book.dto.deserializer.BookSerializer
import kotlinx.serialization.Serializable

@Serializable
data class BookWorksResponse(
    @Serializable(with = BookSerializer::class)
    val description: String? = null,

    val key: String,
    val title: String,
    val covers: List<Int>? = null,
    val authors: List<AuthorWrapper>? = null,
)

@Serializable
data class AuthorWrapper(
    val author: AuthorDto,
    val type: AuthorType,
)

@Serializable
data class AuthorType(
    val key: String,
)