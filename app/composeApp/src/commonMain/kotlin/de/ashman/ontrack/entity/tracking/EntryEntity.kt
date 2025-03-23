package de.ashman.ontrack.entity.tracking

import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class EntryEntity(
    val status: TrackStatus,
    val rating: Double? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,
    val timestamp: Long,
)