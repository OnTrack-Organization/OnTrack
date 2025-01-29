package de.ashman.ontrack.api.movie.dto

import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponseDto(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val parts: List<MovieDto>,
)