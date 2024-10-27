package de.ashman.ontrack.media.domain

import kotlinx.serialization.Serializable

@Serializable
data class VideoGame(
    override val id: String,
    override val type: MediaType = MediaType.VIDEOGAME,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = ConsumeStatus.CATALOG,
    override val userRating: Float = 0F,
    override val coverUrl: String,
    val firstReleaseDate: String?,
    val franchises: List<Franchise>?,
    val genres: List<String>?,
    val platforms: List<Platform>?,
    val totalRating: Double?,
    val totalRatingCount: Int?,
    val similarGames: List<SimilarGame>?,
    val summary: String?,
) : Media()

@Serializable
data class Franchise(
    val name: String,
    val games: List<SimilarGame>,
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