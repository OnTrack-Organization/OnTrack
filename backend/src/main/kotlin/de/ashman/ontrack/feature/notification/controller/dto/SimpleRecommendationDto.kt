package de.ashman.ontrack.feature.notification.controller.dto

import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.tracking.controller.dto.MediaDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto

data class SimpleRecommendationDto(
    val media: MediaDto,
    val message: String?,
)

fun Recommendation.toSimpleDto() = SimpleRecommendationDto(
    media = media.toDto(),
    message = message,
)