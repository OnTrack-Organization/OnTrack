package de.ashman.ontrack.media.show.model.domain

import de.ashman.ontrack.media.common.model.Genre
import de.ashman.ontrack.media.common.model.ProductionCompany
import de.ashman.ontrack.media.common.model.ProductionCountry
import de.ashman.ontrack.media.common.model.SpokenLanguage

data class Show(
    val adult: Boolean?,
    val backdropPath: String?,
    val createdBy: List<CreatedBy>?,
    val episodeRunTime: List<Int>?,
    val firstAirDate: String?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Int,
    val inProduction: Boolean?,
    val languages: List<String>?,
    val lastAirDate: String?,
    val lastEpisodeToAir: LastEpisodeToAir?,
    val name: String,
    val nextEpisodeToAir: String?,
    val networks: List<Network>?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val productionCompanies: List<ProductionCompany>?,
    val productionCountries: List<ProductionCountry>?,
    val seasons: List<Season>?,
    val spokenLanguages: List<SpokenLanguage>?,
    val status: String?,
    val tagline: String?,
    val type: String?,
    val voteAverage: Double?,
    val voteCount: Int?
)

data class CreatedBy(
    val id: Int,
    val creditId: String,
    val name: String,
    val originalName: String,
    val gender: Int,
    val profilePath: String?
)

data class LastEpisodeToAir(
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

data class Network(
    val id: Int,
    val logoPath: String?,
    val name: String,
    val originCountry: String
)

data class Season(
    val airDate: String?,
    val episodeCount: Int,
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int,
    val voteAverage: Double
)
