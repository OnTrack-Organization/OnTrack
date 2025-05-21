package de.ashman.ontrack.tracking.controller.dto

import de.ashman.ontrack.tracking.domain.TrackStatus
import java.util.*

data class UpdateTrackingDto(
    val id: UUID,
    val status: TrackStatus
)
