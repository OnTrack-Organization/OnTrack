package de.ashman.ontrack.media.videogame.model.domain

data class VideoGame(
    val id: Int,
    val coverUrl: String?,
    val firstReleaseDate: String?,
    val franchises: List<Franchise>?,
    val genres: List<String>?,
    val name: String,
    val platforms: List<Platform>?,
    val totalRating: Double?,
    val totalRatingCount: Int?,
    val similarGames: List<SimilarGame>?,
    val summary: String?,
)

data class Franchise(
    val name: String,
    val games: List<SimilarGame>,
)

data class Platform(
    val abbreviation: String,
    val name: String,
    val platformLogo: String?,
)

data class SimilarGame(
    val name: String,
    val cover: String?,
)
