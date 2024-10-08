package de.ashman.ontrack.media.movie.model.entity

import de.ashman.ontrack.media.MediaEntity
import de.ashman.ontrack.shelf.StatusType
import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    override val id: String,
    override val type: String = "movie",
    val backdropPath: String?,
    val collection: CollectionEntity?,
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

    val watchStatus: StatusType,
) : MediaEntity

@Serializable
data class CollectionEntity(
    val id: Int,
    val name: String?,
    val posterPath: String?,
    val backdropPath: String?
)