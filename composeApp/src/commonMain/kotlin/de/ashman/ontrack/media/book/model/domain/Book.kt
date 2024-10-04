package de.ashman.ontrack.media.book.model.domain

data class Book(
    val key: String = "",
    val authorKey: List<String>? = null,
    val authorName: List<String>? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val firstPublishYear: Int? = null,
    val firstSentence: List<String>? = null,
    val language: List<String>? = null,
    val numberOfPagesMedian: Int? = null,
    val person: List<String>? = null,
    val place: List<String>? = null,
    val publisher: List<String>? = null,
    val ratingsAverage: Double? = null,
    val ratingsCount: Int? = null,
    val subject: List<String>? = null,
    val title: String? = null,
)
