package de.ashman.ontrack.recommendation.controller.dto

import de.ashman.ontrack.tracking.application.controller.MediaDto

data class CreateRecommendationDto(
    val userId: String,
    val media: MediaDto,
    val message: String?,
)