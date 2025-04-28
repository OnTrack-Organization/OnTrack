package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.model.TrackStatus
import java.util.*

data class UpdateTrackingDto(
    val id: UUID,
    val status: TrackStatus
)
