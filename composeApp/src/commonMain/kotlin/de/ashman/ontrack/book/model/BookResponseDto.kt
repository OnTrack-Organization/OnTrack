package de.ashman.ontrack.book.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookResponseDto(
    @SerialName("works")
    val books: List<BookDto>? = null,

    @SerialName("key")
    val key: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("subject_type")
    val subjectType: String? = null,
    @SerialName("work_count")
    val workCount: Int? = null,
)