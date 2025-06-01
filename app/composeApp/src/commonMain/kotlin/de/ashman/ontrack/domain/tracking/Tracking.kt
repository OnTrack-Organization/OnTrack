package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.database.tracking.TrackingEntity
import de.ashman.ontrack.domain.media.MediaData

data class Tracking(
    val id: String,
    val userId: String,
    val media: MediaData,
    val status: TrackStatus,
    val timestamp: Long,
)

fun Tracking.toEntity() = TrackingEntity(
    id = id,
    userId = userId,
    media = media,
    status = status,
    timestamp = timestamp,
)

const val MAX_RATING = 5