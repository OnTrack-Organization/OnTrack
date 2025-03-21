package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val key: String,
    val title: String,
    val coverI: Int? = null,
    val description: String? = null,
    val firstPublishYear: Int? = null,
    val authorKey: List<String>? = null,
    val authorName: List<String>? = null,
    val firstSentence: List<String>? = null,
    val language: List<String>? = null,
    val numberOfPagesMedian: Int? = null,
    val person: List<String>? = null,
    val place: List<String>? = null,
    val publisher: List<String>? = null,
    val subject: List<String>? = null,
)
