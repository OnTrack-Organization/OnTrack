package de.ashman.ontrack.api.album.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExternalUrlsDto(
    val spotify: String
)

@Serializable
data class ImageDto(
    val url: String,
    val height: Int,
    val width: Int
)
