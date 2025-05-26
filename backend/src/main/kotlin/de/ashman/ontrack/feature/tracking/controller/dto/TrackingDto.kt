package de.ashman.ontrack.feature.tracking.controller.dto

import de.ashman.ontrack.feature.tracking.domain.TrackStatus
import de.ashman.ontrack.feature.tracking.domain.Tracking
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
    userId = user.id,
    media = media.toDto(),
    status = status,
    timestamp = updatedAt.toEpochMilli(),
)
