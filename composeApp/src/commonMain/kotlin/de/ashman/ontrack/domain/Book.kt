package de.ashman.ontrack.domain

import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_pages
import org.jetbrains.compose.resources.getPluralString

@Serializable
data class Book(
    override val id: String,
    override val mediaType: MediaType = MediaType.BOOK,
    override val title: String,
    override val coverUrl: String,
    override val releaseYear: String?,
    override val trackStatus: TrackStatus? = null,
    val description: String? = null,
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
    val genres: List<String>? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        numberOfPagesMedian?.let { infoItems.add(getPluralString(Res.plurals.detail_pages, it, it)) }
        authors?.let { infoItems.add(it.first()) }

        return infoItems
    }
}
