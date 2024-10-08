package de.ashman.ontrack.media.videogame.model.entity

import de.ashman.ontrack.media.MediaEntity
import kotlinx.serialization.Serializable

@Serializable
data class VideoGameEntity(
    override val id: String,
    override val type: String = "videogame",
    val coverUrl: String?,
    val firstReleaseDate: Long?,
    val franchises: List<FranchiseEntity>?,
    val genres: List<String>?,
    val name: String,
    val platforms: List<PlatformEntity>?,
    val totalRating: Double?,
    val totalRatingCount: Int?,
    val similarGames: List<SimilarGameEntity>?,
    val summary: String?,
) : MediaEntity

@Serializable
data class FranchiseEntity(
    val name: String,
    val games: List<SimilarGameEntity>,
)

@Serializable
data class PlatformEntity(
    val abbreviation: String,
    val name: String,
    val platformLogo: String?,
)

@Serializable
data class SimilarGameEntity(
    val name: String,
    val cover: String?,
)
