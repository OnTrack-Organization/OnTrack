package de.ashman.ontrack.api.movie.dto

import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponseDto(
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val parts: List<MovieDto>? = null,
)