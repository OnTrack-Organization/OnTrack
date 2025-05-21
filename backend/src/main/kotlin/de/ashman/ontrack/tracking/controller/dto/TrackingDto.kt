package de.ashman.ontrack.tracking.controller.dto

import de.ashman.ontrack.tracking.domain.TrackStatus
import de.ashman.ontrack.tracking.domain.Tracking
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
