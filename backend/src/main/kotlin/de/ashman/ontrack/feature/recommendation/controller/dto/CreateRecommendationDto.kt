package de.ashman.ontrack.feature.recommendation.controller.dto

import de.ashman.ontrack.feature.tracking.controller.dto.MediaDto

data class CreateRecommendationDto(
    val userId: String,
    val media: MediaDto,
    val message: String?,
)