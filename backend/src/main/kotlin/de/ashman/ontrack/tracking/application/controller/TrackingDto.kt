package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.model.TrackStatus
import de.ashman.ontrack.tracking.domain.model.Tracking
import java.time.LocalDateTime
import java.util.*

data class TrackingDto(
    val id: UUID,
    val userId: String,
    val media: MediaDto,
    val status: TrackStatus,
    val timestamp: LocalDateTime
)

fun Tracking.toDto() = TrackingDto(
    id,
    userId,
    MediaDto(
        media.id,
        media.type,
        media.title,
        media.coverUrl
    ),
    status,
    createdAt
)
