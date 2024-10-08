package de.ashman.ontrack.media.show.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    val id: Int,
    val backdropPath: String? = null,
    val episodeRunTime: List<Int>? = null,
    val firstAirDate: String? = null,
    val genres: List<GenreDto>? = null,
    val languages: List<String>? = null,
    val name: String? = null,
    val numberOfEpisodes: Int? = null,
    val numberOfSeasons: Int? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val seasons: List<SeasonDto>? = null,
    val status: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)

@Serializable
data class SeasonDto(
    val id: Int,
    val airDate: String?,
    val episodeCount: Int?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int?,
    val voteAverage: Double?
)
