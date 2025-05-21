package de.ashman.ontrack.recommendation.controller.dto

import de.ashman.ontrack.tracking.controller.dto.MediaDto

data class CreateRecommendationDto(
    val userId: String,
    val media: MediaDto,
    val message: String?,
)