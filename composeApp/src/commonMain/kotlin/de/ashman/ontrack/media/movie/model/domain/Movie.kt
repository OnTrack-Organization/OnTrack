package de.ashman.ontrack.media.movie.model.domain

import de.ashman.ontrack.shelf.StatusType

data class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String? = null,
    val originalLanguage: String? = null,
    val overview: String? = null,

    val backdropPath: String? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,

    val adult: Boolean? = null,
    val runtime: Int? = null,
    val status: String? = null,
    val voteAverage: Double? = null,

    // OWN FIELDS
    // Default to Watched
    val watchStatus: StatusType = StatusType.WATCHED,
)