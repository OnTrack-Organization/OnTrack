package de.ashman.ontrack.entity

import de.ashman.ontrack.domain.sub.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaEntity(
    val id: String,
    val name: String,
    val coverUrl: String,
    val type: MediaType,
    val trackStatus: TrackStatusEntity?,
)
