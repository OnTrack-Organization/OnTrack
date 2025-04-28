package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.model.TrackStatus

data class CreateTrackingDto(
    val status: TrackStatus,
    val media: MediaDto
)
