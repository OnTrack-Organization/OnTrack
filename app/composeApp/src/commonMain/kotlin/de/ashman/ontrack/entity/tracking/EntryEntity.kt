package de.ashman.ontrack.entity.tracking

import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class EntryEntity(
    val status: TrackStatus,
    val timestamp: Long,
)