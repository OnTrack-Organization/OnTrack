package de.ashman.ontrack.domain.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaData(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String?,
)
