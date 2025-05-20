package de.ashman.ontrack.recommendation.controller.dto

import de.ashman.ontrack.recommendation.domain.Recommendation
import de.ashman.ontrack.user.application.controller.user.UserDto
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