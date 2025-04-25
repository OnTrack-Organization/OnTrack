package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.model.TrackStatus

data class TrackingCreateDto(
    val status: TrackStatus,
    val media: MediaDto
)
