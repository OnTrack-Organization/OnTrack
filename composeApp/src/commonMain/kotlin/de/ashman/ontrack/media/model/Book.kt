package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    override val id: String,
    override val type: MediaType = MediaType.BOOK,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    override val releaseYear: String?,
    val authorKey: List<String>? = null,
    val authorName: List<String>? = null,
    val description: String? = null,
    val firstSentence: List<String>? = null,
    val language: List<String>? = null,
    val numberOfPagesMedian: Int? = null,
    val person: List<String>? = null,
    val place: List<String>? = null,
    val publisher: List<String>? = null,
    val ratingsAverage: Double? = null,
    val ratingsCount: Int? = null,
    val subject: List<String>? = null,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        numberOfPagesMedian?.let { infoItems.add("$it Pages") }
        authorName?.let { infoItems.add("$it") }

        return infoItems
    }
}
