package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoGame(
    override val id: String,
    override val type: MediaType = MediaType.VIDEOGAME,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    override val releaseYear: String?,
    val franchises: List<Franchise>?,
    val genres: List<String>?,
    val platforms: List<Platform>?,
    val totalRating: Double?,
    val totalRatingCount: Int?,
    val similarGames: List<SimilarGame>?,
    val summary: String?,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        platforms?.let { infoItems.add(it.first().name) }

        return infoItems
    }
}

@Serializable
data class Franchise(
    val name: String,
    val games: List<SimilarGame>?,
)

@Serializable
data class Platform(
    val abbreviation: String,
    val name: String,
    val platformLogo: String?,
)

@Serializable
data class SimilarGame(
    val name: String,
    val cover: String?,
)