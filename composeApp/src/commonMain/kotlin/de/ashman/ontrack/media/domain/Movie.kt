package de.ashman.ontrack.media.domain

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    override val id: String,
    override val type: MediaType = MediaType.MOVIE,
    override val name: String,
    override val consumeStatus: StatusType? = StatusType.CATALOG,
    override val userRating: Float? = null,
    override val coverUrl: String,
    val backdropPath: String?,
    val genres: List<String>?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    val releaseDate: String?,
    val revenue: Long?,
    val runtime: Int?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
) : Media()
