package de.ashman.ontrack.movie.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String? = null,
    val originalTitle: String? = null,
    val originalLanguage: String? = null,
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,

    val status: MovieStatus? = null,
)

@Serializable
enum class MovieStatus {
    WATCHING,
    COMPLETED,
    WANT_TO_WATCH,
    DROPPED,
}