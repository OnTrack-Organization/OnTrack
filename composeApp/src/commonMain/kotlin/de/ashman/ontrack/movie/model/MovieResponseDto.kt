package de.ashman.ontrack.movie.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    @SerialName("results")
    val movies: List<MovieDto>? = null,

    val page: Int? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null,
)