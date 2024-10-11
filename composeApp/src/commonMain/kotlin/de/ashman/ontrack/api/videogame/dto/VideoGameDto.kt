package de.ashman.ontrack.api.videogame.dto

import kotlinx.serialization.Serializable

// https://api-docs.igdb.com/#game
@Serializable
data class VideoGameDto(
    val id: Int,
    val cover: CoverDto? = null,
    val firstReleaseDate: Long? = null,
    val franchises: List<FranchiseDto>? = null,
    val genres: List<GenreDto>? = null,
    val name: String,
    val platforms: List<PlatformDto>? = null,
    val totalRating: Double? = null,
    val totalRatingCount: Int? = null,
    val similarGames: List<SimilarGameDto>? = null,
    val summary: String? = null,
)

@Serializable
data class CoverDto(
    val url: String?,
)

@Serializable
data class FranchiseDto(
    val name: String,
    val games: List<SimilarGameDto>,
)

@Serializable
data class GenreDto(
    val name: String,
)

@Serializable
data class PlatformDto(
    val abbreviation: String,
    val name: String,
    val platformLogo: PlatformLogoDto? = null,
)

@Serializable
data class PlatformLogoDto(
    val url: String
)

@Serializable
data class SimilarGameDto(
    val name: String,
    val cover: CoverDto? = null,
)
