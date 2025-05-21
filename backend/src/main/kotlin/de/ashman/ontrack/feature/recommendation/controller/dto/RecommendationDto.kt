package de.ashman.ontrack.feature.recommendation.controller.dto

import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import java.time.ZoneOffset

data class RecommendationDto(
    val user: UserDto,
    val message: String?,
    val timestamp: Long,
)

fun Recommendation.toDto(userDto: UserDto) = RecommendationDto(
    user = userDto,
    message = message,
    timestamp = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
)