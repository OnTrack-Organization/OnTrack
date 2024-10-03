package de.ashman.ontrack.media.movie.model.dto

import de.ashman.ontrack.media.common.model.GenreDto
import kotlinx.serialization.Serializable

// https://developer.themoviedb.org/reference/intro/getting-started
// TODO get similar movies
@Serializable
data class MovieDto(
    val id: Int,
    val backdropPath: String? = null,
    val belongsToCollection: CollectionDto? = null,
    val genres: List<GenreDto>? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val status: String? = null,
    val title: String,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
)

@Serializable
data class CollectionDto(
    val id: Int,
    val name: String,
    val posterPath: String,
    val backdropPath: String
)
