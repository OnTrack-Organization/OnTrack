package de.ashman.ontrack.media.show.model.entity

import de.ashman.ontrack.media.common.model.GenreEntity
import de.ashman.ontrack.media.common.model.ProductionCompanyEntity
import de.ashman.ontrack.media.common.model.ProductionCountryEntity
import de.ashman.ontrack.media.common.model.SpokenLanguageEntity
import kotlinx.serialization.Serializable

@Serializable
data class ShowEntity(
    val id: Int,
    val adult: Boolean?,
    val backdropPath: String?,
    val createdBy: List<CreatedByEntity>?,
    val episodeRunTime: List<Int>?,
    val firstAirDate: String?,
    val genres: List<GenreEntity>?,
    val homepage: String?,
    val inProduction: Boolean?,
    val languages: List<String>?,
    val lastAirDate: String?,
    val lastEpisodeToAir: LastEpisodeToAirEntity?,
    val name: String,
    val nextEpisodeToAir: String?,
    val networks: List<NetworkEntity>?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val productionCompanies: List<ProductionCompanyEntity>?,
    val productionCountries: List<ProductionCountryEntity>?,
    val seasons: List<SeasonEntity>?,
    val spokenLanguages: List<SpokenLanguageEntity>?,
    val status: String?,
    val tagline: String?,
    val type: String?,
    val voteAverage: Double?,
    val voteCount: Int?
)

@Serializable
data class CreatedByEntity(
    val id: Int,
    val creditId: String,
    val name: String,
    val originalName: String,
    val gender: Int,
    val profilePath: String?
)

@Serializable
data class LastEpisodeToAirEntity(
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
data class NetworkEntity(
    val id: Int,
    val logoPath: String?,
    val name: String,
    val originCountry: String
)

@Serializable
data class SeasonEntity(
    val airDate: String?,
    val episodeCount: Int,
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int,
    val voteAverage: Double
)
