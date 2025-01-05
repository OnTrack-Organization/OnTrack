package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
data class Show(
    override val id: String,
    override val type: MediaType = MediaType.SHOW,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    val backdropPath: String?,
    val episodeRunTime: List<Int>?,
    val firstAirDate: String?,
    val genres: List<String>?,
    val languages: List<String>?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    val popularity: Double?,
    val seasons: List<Season>?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?
) : Media()

@Serializable
data class Season(
    val id: Int,
    val airDate: String?,
    val episodeCount: Int?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int?,
    val voteAverage: Double?
)
