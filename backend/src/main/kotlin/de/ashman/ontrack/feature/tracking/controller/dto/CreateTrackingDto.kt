package de.ashman.ontrack.feature.tracking.controller.dto

import de.ashman.ontrack.feature.tracking.domain.TrackStatus

data class CreateTrackingDto(
    val status: TrackStatus,
    val media: MediaDto
)
