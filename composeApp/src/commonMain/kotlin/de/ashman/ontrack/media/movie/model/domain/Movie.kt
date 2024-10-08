package de.ashman.ontrack.media.movie.model.domain

import de.ashman.ontrack.shelf.StatusType

data class Movie(
    val id: String,
    val backdropPath: String?,
    val genres: List<String>?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val releaseDate: String?,
    val revenue: Long?,
    val runtime: Int?,
    val status: String?,
    val title: String?,
    val voteAverage: Double?,
    val voteCount: Int?,

    val watchStatus: StatusType = StatusType.WATCHED,
)
