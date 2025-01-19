package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.TrackStatusType
import kotlinx.serialization.Serializable

@Serializable
data class MediaEntity(
    val id: String,
    val name: String,
    val coverUrl: String,
    val type: MediaType,
    val trackStatus: TrackStatusEntity?,
)

@Serializable
data class TrackStatusEntity(
    val id: String,
    val statusType: TrackStatusType?,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
)