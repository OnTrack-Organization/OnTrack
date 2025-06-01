package de.ashman.ontrack.feature.recommendation.controller.dto

import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.tracking.controller.dto.MediaDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import java.time.ZoneOffset

data class RecommendationDto(
    val user: UserDto,
    val media: MediaDto,
    val message: String?,
    val timestamp: Long,
)

fun Recommendation.toDto(userDto: UserDto) = RecommendationDto(
    user = userDto,
    media = media.toDto(),
    message = message,
    timestamp = createdAt.toEpochMilli(),
)