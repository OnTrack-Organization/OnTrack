package de.ashman.ontrack.api.book.dto

import de.ashman.ontrack.api.book.dto.deserializer.BookSerializer
import de.ashman.ontrack.api.book.dto.deserializer.TypeSerializer
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
    @Serializable(with = TypeSerializer::class)
    val type: String? = null,
)
