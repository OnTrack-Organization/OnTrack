package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    override val id: String,
    override val type: MediaType = MediaType.MOVIE,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    override val releaseYear: String?,
    val backdropPath: String?,
    val genres: List<String>?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    val revenue: Long?,
    val runtime: Int?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        runtime?.let { infoItems.add("$it Min") }

        return infoItems
    }
}
