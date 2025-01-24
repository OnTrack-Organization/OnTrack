package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaEntity(
    val id: String,
    val name: String,
    val coverUrl: String,
    val type: MediaType,
    val trackStatus: TrackStatusEntity?,
)
