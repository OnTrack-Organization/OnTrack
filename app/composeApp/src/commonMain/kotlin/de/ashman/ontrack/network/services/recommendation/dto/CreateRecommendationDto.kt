package de.ashman.ontrack.network.services.recommendation.dto

import de.ashman.ontrack.network.services.tracking.dto.MediaDataDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecommendationDto(
    val userId: String,
    val media: MediaDataDto,
    val message: String?,
)
