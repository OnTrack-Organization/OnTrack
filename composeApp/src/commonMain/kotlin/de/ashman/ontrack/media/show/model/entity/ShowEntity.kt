package de.ashman.ontrack.media.show.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class ShowEntity(
    val id: Int,
    val backdropPath: String?,
    val episodeRunTime: List<Int>?,
    val firstAirDate: String?,
    val genres: List<GenreEntity>?,
    val languages: List<String>?,
    val name: String?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val seasons: List<SeasonEntity>?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?
)

@Serializable
data class GenreEntity(
    val id: Int,
    val name: String?
)

@Serializable
data class SeasonEntity(
    val id: Int,
    val airDate: String?,
    val episodeCount: Int?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int?,
    val voteAverage: Double?
)
