package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    override val id: String,
    override val mediaType: MediaType = MediaType.BOOK,
    override val name: String,
    override val coverUrl: String,
    override val releaseYear: String?,
    override val trackStatus: TrackStatus? = null,
    val description: String?,
    val authorKeys: List<String>? = null,
    val authors: List<String>? = null,
    val firstSentence: List<String>? = null,
    val language: List<String>? = null,
    val numberOfPagesMedian: Int? = null,
    val person: List<String>? = null,
    val place: List<String>? = null,
    val publisher: List<String>? = null,
    val ratingsAverage: Double? = null,
    val ratingsCount: Int? = null,
    val subjects: List<String>? = null,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        numberOfPagesMedian?.let { infoItems.add("$it Pages") }
        authors?.let { infoItems.add(it.first()) }

        return infoItems
    }
}
