package de.ashman.ontrack.login.model

import de.ashman.ontrack.media.movie.model.MovieDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val movies: List<MovieDto> = emptyList(),
    val friends: Map<String, Boolean> = emptyMap(),  // Friends stored as a map of user IDs
    val reviews: Map<String, Boolean> = emptyMap()  // Reviews stored as a map of review IDs
)
