package de.ashman.ontrack.network.services.tracking.dto

import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.serialization.Serializable

@Serializable
data class TrackingDto(
    val id: String,
    val userId: String,
    val media: MediaDto,
    val status: TrackStatus,
    val timestamp: Long,
)

fun TrackingDto.toDomain() = Tracking(
    id = id,
    userId = userId,
    media = media.toDomain(),
    status = status,
    timestamp = timestamp,
)