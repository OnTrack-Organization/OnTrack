package de.ashman.ontrack.media.videogame.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class VideoGameEntity(
    val id: Int,
    val cover: CoverEntity?,
    val firstReleaseDate: Long?,
    val franchises: List<FranchiseEntity>?,
    val genres: List<GenreEntity>?,
    val name: String,
    val platforms: List<PlatformEntity>?,
    val totalRating: Double?,
    val totalRatingCount: Int?,
    val similarGames: List<SimilarGameEntity>?,
    val summary: String?,
)

@Serializable
data class CoverEntity(
    val url: String?,
)

@Serializable
data class FranchiseEntity(
    val name: String,
    val games: List<SimilarGameEntity>,
)

@Serializable
data class GenreEntity(
    val name: String,
)

@Serializable
data class PlatformEntity(
    val abbreviation: String,
    val name: String,
    val platformLogo: PlatformLogoEntity?,
)

@Serializable
data class PlatformLogoEntity(
    val url: String
)

@Serializable
data class SimilarGameEntity(
    val name: String,
    val cover: CoverEntity?,
)
