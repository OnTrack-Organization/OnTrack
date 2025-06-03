package de.ashman.ontrack.network.services.recommendation.dto

import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDto(
    val user: UserDto,
    val message: String,
    val timestamp: Long,
)

fun RecommendationDto.toDomain() = Recommendation(
    user = user.toDomain(),
    message = message,
    timestamp = timestamp,
)
