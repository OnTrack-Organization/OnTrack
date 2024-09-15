package de.ashman.ontrack.media.movie.model

data class Movie(
    val id: Int? = null,
    val title: String? = null,
    val originalTitle: String? = null,
    val category: String? = null,

    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val originalLanguage: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,

    val isFavorite: Boolean? = null,
    val cacheId: Int? = 0,
)