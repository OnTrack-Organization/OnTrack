package de.ashman.ontrack.network.services.notification.dto

import de.ashman.ontrack.domain.notification.SimpleRecommendation
import de.ashman.ontrack.network.services.tracking.dto.MediaDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class SimpleRecommendationDto(
    val media: MediaDto,
    val message: String?,
)

fun SimpleRecommendationDto.toDomain() = SimpleRecommendation(
    media = media.toDomain(),
    message = message,
)