package de.ashman.ontrack.media.videogame.model.entity

import de.ashman.ontrack.media.MediaEntity
import de.ashman.ontrack.media.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class VideoGameEntity(
    override val id: String,
    override val type: MediaType = MediaType.VIDEOGAME,
    override val name: String,
    override val coverUrl: String,
    val firstReleaseDate: String?,
    val franchises: List<FranchiseEntity>?,
    val genres: List<String>?,
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
