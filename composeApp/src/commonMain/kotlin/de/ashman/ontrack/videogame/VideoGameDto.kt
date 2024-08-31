package de.ashman.ontrack.videogame

import kotlinx.serialization.Serializable

@Serializable
class VideoGameDto(
    val id: String,
    val name: String? = null
)
