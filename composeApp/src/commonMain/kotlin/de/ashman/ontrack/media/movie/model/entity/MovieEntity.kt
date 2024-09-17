package de.ashman.ontrack.media.movie.model.entity

import de.ashman.ontrack.media.movie.model.domain.MovieStatus
import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    val id: Int? = null,
    val title: String? = null,
    val originalTitle: String? = null,
    val originalLanguage: String? = null,
    val overview: String? = null,

    val backdropPath: String? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,

    val adult: Boolean? = null,
    val popularity: Double? = null,
    val video: Boolean? = null,

    val voteAverage: Double? = null,
    val voteCount: Int? = null,

    val status: MovieStatus? = null,
)
