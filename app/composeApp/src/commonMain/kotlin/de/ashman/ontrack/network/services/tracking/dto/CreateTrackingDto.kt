package de.ashman.ontrack.network.services.tracking.dto

import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class CreateTrackingDto(
    val media: MediaDataDto,
    val status: TrackStatus,
)