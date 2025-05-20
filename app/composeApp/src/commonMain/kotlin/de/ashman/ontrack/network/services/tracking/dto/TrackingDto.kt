package de.ashman.ontrack.network.services.tracking.dto

import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class TrackingDto(
    val id: String,
    val userId: String,
    val media: MediaDataDto,
    val status: TrackStatus,
    val timestamp: Long,
)

fun TrackingDto.toDomain() = NewTracking(
    id = id,
    userId = userId,
    media = media.toDomain(),
    status = status,
    timestamp = timestamp,
)