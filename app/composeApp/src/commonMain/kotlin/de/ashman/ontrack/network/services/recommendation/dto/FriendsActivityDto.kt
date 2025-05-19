package de.ashman.ontrack.network.services.recommendation.dto

import de.ashman.ontrack.domain.recommendation.FriendsActivity
import kotlinx.serialization.Serializable

@Serializable
data class FriendsActivityDto(
    val recommendations: List<RecommendationDto>,
    val trackings: List<SimpleTrackingDto>,
)

fun FriendsActivityDto.toDomain(): FriendsActivity? {
    if (recommendations.isEmpty() && trackings.isEmpty()) return null

    return FriendsActivity(
        recommendations = recommendations.map { it.toDomain() },
        trackings = trackings.map { it.toDomain() },
    )
}

