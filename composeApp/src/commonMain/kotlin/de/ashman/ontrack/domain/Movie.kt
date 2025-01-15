package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.sub.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    override val id: String,
    override val mediaType: MediaType = MediaType.MOVIE,
    override val name: String,
    override val coverUrl: String,
    override val releaseYear: String?,
    val description: String?,
    val backdropPath: String?,
    val genres: List<String>?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val popularity: Double?,
    val revenue: Long?,
    val runtime: Int?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val similarMovies: List<Movie>? = null,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        runtime?.let { infoItems.add("$it Min") }

        return infoItems
    }
}
