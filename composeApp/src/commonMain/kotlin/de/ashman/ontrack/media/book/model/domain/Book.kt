package de.ashman.ontrack.media.book.model.domain

import de.ashman.ontrack.media.Media
import de.ashman.ontrack.media.MediaType
import de.ashman.ontrack.media.StatusType
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    override val id: String,
    override val type: MediaType = MediaType.BOOK,
    override val name: String,
    override val consumeStatus: StatusType? = null,
    override val coverUrl: String,
    val authorKey: List<String>? = null,
    val authorName: List<String>? = null,
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
) : Media
