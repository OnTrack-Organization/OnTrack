package de.ashman.ontrack.media.movie.model.entity

import de.ashman.ontrack.media.MediaEntity
import de.ashman.ontrack.media.MediaType
import de.ashman.ontrack.media.StatusType
import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    override val id: String,
    override val type: MediaType = MediaType.MOVIE,
    override val name: String,
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

    val watchStatus: StatusType,
) : MediaEntity
