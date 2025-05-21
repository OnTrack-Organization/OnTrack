package de.ashman.ontrack.feature.tracking.controller.dto

import de.ashman.ontrack.feature.tracking.domain.TrackStatus
import java.util.*

data class UpdateTrackingDto(
    val id: UUID,
    val status: TrackStatus
)
