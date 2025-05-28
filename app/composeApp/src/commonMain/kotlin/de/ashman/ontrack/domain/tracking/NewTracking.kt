package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.database.tracking.NewTrackingEntity
import de.ashman.ontrack.domain.media.MediaData

data class NewTracking(
    val id: String,
    val userId: String,
    val media: MediaData,
    val status: TrackStatus,
    val timestamp: Long,
)

fun NewTracking.toEntity() = NewTrackingEntity(
    id = id,
    userId = userId,
    media = media,
    status = status,
    timestamp = timestamp,
)

const val MAX_RATING = 5