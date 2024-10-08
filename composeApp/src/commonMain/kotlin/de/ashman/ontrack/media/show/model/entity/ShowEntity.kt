package de.ashman.ontrack.media.show.model.entity

import de.ashman.ontrack.media.MediaEntity
import kotlinx.serialization.Serializable

@Serializable
data class ShowEntity(
    override val id: String,
    override val type: String = "show",
    val backdropPath: String?,
    val episodeRunTime: List<Int>?,
    val firstAirDate: String?,
    val genres: List<String>?,
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
) : MediaEntity

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
