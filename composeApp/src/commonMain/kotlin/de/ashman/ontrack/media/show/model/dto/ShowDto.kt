package de.ashman.ontrack.media.show.model.dto

import de.ashman.ontrack.media.common.model.GenreDto
import de.ashman.ontrack.media.common.model.ProductionCompanyDto
import de.ashman.ontrack.media.common.model.ProductionCountryDto
import de.ashman.ontrack.media.common.model.SpokenLanguageDto
import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val createdBy: List<CreatedByDto>? = null,
    val episodeRunTime: List<Int>? = null,
    val firstAirDate: String? = null,
    val genres: List<GenreDto>? = null,
    val homepage: String? = null,
    val id: Int,
    val inProduction: Boolean? = null,
    val languages: List<String>? = null,
    val lastAirDate: String? = null,
    val lastEpisodeToAir: LastEpisodeToAirDto? = null,
    val name: String,
    val nextEpisodeToAir: String? = null, // Could use a specific DTO if details available
    val networks: List<NetworkDto>? = null,
    val numberOfEpisodes: Int? = null,
    val numberOfSeasons: Int? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompanyDto>? = null,
    val productionCountries: List<ProductionCountryDto>? = null,
    val seasons: List<SeasonDto>? = null,
    val spokenLanguages: List<SpokenLanguageDto>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val type: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)

@Serializable
data class CreatedByDto(
    val id: Int,
    val creditId: String,
    val name: String,
    val originalName: String,
    val gender: Int,
    val profilePath: String?
)

@Serializable
data class LastEpisodeToAirDto(
    val id: Int,
    val name: String,
    val overview: String,
    val voteAverage: Double,
    val voteCount: Int,
    val airDate: String?,
    val episodeNumber: Int,
    val episodeType: String,
    val productionCode: String,
    val runtime: Int?,
    val seasonNumber: Int,
    val showId: Int,
    val stillPath: String?
)

@Serializable
data class NetworkDto(
    val id: Int,
    val logoPath: String?,
    val name: String,
    val originCountry: String
)

@Serializable
data class SeasonDto(
    val airDate: String?,
    val episodeCount: Int,
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int,
    val voteAverage: Double
)
