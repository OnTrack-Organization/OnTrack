package de.ashman.ontrack.api.movie.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    @SerialName("results")
    val movies: List<MovieDto> = emptyList(),

    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
)