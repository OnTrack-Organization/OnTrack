package de.ashman.ontrack.domain.newdomains

import de.ashman.ontrack.domain.tracking.TrackStatus

data class NewTracking(
    val id: String,
    val userId: String,
    val media: MediaData,
    val status: TrackStatus,
    val timestamp: Long,
)