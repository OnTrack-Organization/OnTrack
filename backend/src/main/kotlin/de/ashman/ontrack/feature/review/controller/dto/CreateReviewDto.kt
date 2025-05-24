package de.ashman.ontrack.feature.review.controller.dto

import java.util.UUID

data class CreateReviewDto(
    val trackingId: UUID,
    val rating: Double,
    val title: String?,
    val description: String?,
)