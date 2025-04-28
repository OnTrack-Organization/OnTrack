package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.model.TrackStatus
import de.ashman.ontrack.tracking.domain.model.Tracking
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class TrackingDto(
    val id: UUID,
    val userId: String,
    val media: MediaDto,
    val status: TrackStatus,
    val timestamp: Long,
)

fun Tracking.toDto() = TrackingDto(
    id = id,
    userId = userId,
    media = media.toDto(),
    status = status,
    timestamp = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
)
