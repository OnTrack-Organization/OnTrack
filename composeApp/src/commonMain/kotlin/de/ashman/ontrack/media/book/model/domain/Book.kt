package de.ashman.ontrack.media.book.model.domain

data class Book(
    val key: String,
    val authorKey: List<String>?,
    val authorName: List<String>?,
    val coverUrl: String?,
    val description: String?,
    val firstPublishYear: Int?,
    val firstSentence: List<String>?,
    val language: List<String>?,
    val numberOfPagesMedian: Int?,
    val person: List<String>?,
    val place: List<String>?,
    val publisher: List<String>?,
    val ratingsAverage: Double?,
    val ratingsCount: Int?,
    val subject: List<String>?,
    val title: String?,
)
