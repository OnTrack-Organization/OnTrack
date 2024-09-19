package de.ashman.ontrack.media.movie.model.entity

import de.ashman.ontrack.media.movie.model.domain.WatchStatus
import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    val id: Int,
    val title: String? = null,
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

    val watchStatus: WatchStatus? = null,
)
