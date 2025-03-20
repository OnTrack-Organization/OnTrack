package de.ashman.ontrack.api.movie.dto

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val posterPath: String? = null,
    val overview: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val releaseDate: String? = null,

    val backdropPath: String? = null,
    val belongsToCollection: CollectionDto? = null,
    val genres: List<GenreDto>? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val popularity: Double? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val status: String? = null,

    val credits: CreditsDto? = null,
    val similar: MovieResponseDto? = null,
    val images: ImageWrapper? = null,
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)

@Serializable
data class CollectionDto(
    val id: Int,
    val name: String?,
    val posterPath: String?,
    val backdropPath: String?
)

@Serializable
data class ImageWrapper(
    val backdrops: List<TMDBImageDto>,
    val logos: List<TMDBImageDto>,
    val posters: List<TMDBImageDto>,
)

@Serializable
data class TMDBImageDto(
    val filePath: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Double,
    val voteAverage: Double,
    val voteCount: Int
)