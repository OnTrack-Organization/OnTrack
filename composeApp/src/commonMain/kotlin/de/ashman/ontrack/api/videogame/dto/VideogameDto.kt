package de.ashman.ontrack.api.videogame.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideogameDto(
    val id: Int? = null,
    val cover: CoverDto? = null,
    val firstReleaseDate: Long? = null,
    val franchises: List<Int>? = null,
    val genres: List<GenreDto>? = null,
    val involvedCompanies: List<InvolvedCompanyDto>? = null,
    val name: String? = null,
    val platforms: List<PlatformDto>? = null,
    val totalRating: Double? = null,
    val totalRatingCount: Int? = null,
    val similarGames: List<VideogameDto>? = null,
    val summary: String? = null,
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
