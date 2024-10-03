package de.ashman.ontrack.media.movie.model.entity

import de.ashman.ontrack.shelf.StatusType
import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    val id: Int,
    val backdropPath: String?,
    val collection: CollectionEntity?,
    val genres: List<GenreEntity>?,
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

    val watchStatus: StatusType,
)

@Serializable
data class GenreEntity(
    val id: Int,
    val name: String?
)

@Serializable
data class CollectionEntity(
    val id: Int,
    val name: String?,
    val posterPath: String?,
    val backdropPath: String?
)