package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.domain.media.MediaData

data class NewTracking(
    val id: String,
    val userId: String,
    val media: MediaData,
    val status: TrackStatus,
    val timestamp: Long,
)