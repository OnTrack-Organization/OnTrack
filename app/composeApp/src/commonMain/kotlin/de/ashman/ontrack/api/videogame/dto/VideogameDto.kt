package de.ashman.ontrack.api.videogame.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideogameDto(
    val id: Int? = null,
    val cover: CoverDto? = null,
    val name: String,
    val summary: String? = null,
    val firstReleaseDate: Long? = null,
    val totalRating: Double? = null,
    val totalRatingCount: Int? = null,

    val franchises: List<Int>? = null,
    val genres: List<GenreDto>? = null,
    val involvedCompanies: List<InvolvedCompanyDto>? = null,
    val platforms: List<PlatformDto>? = null,
    val similarGames: List<VideogameDto>? = null,
    val screenshots: List<ScreenshotDto>? = null,
)

@Serializable
data class FranchiseDto(
    val id: Int,
    val name: String,
    val games: List<VideogameDto>? = null
)

@Serializable
data class CoverDto(
    val id: Int,
    val url: String?,
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String,
)

@Serializable
data class InvolvedCompanyDto(
    val company: CompanyDto,
)

@Serializable
data class CompanyDto(
    val id: Int,
    val name: String,
)

@Serializable
data class PlatformDto(
    val id: Int,
    val abbreviation: String? = null,
    val name: String,
    val platformLogo: PlatformLogoDto? = null,
)

@Serializable
data class PlatformLogoDto(
    val id: Int,
    val url: String,
)

@Serializable
data class PopularityDto(
    val id: Int,
    val gameId: Int,
    val value: Float,
    val popularityType: Int,
)

@Serializable
data class ScreenshotDto(
    @SerialName("image_id")
    val imageId: String? = null,
    val url: String,
)