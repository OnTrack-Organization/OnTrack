package de.ashman.ontrack.tracking.controller.dto

import de.ashman.ontrack.tracking.domain.TrackStatus

data class CreateTrackingDto(
    val status: TrackStatus,
    val media: MediaDto
)
