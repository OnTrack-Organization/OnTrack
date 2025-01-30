package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
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
    override val releaseYear: String? = null,
    override val trackStatus: TrackStatus? = null,
    override val ratingStats: RatingStats? = null,
    override val description: String? = null,
    val author: Author,
    val numberOfPagesMedian: Int? = null,
    val publisher: List<String>? = null,
    val genres: List<String>? = null,
    val ratingsCount: Int? = null,
    val ratingsAverage: Double? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        numberOfPagesMedian?.let { infoItems.add(getPluralString(Res.plurals.detail_pages, it, it)) }
        author.name?.let { infoItems.add(it) }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class Author(
    val id: String,
    val name: String? = null,
    val books: List<Book>? = null,
    val booksCount: Int? = null,
    val imageUrl: String? = null,
    val bio: String? = null,
    val birthDate: String? = null,
    val deathDate: String? = null,
) : CommonParcelable
