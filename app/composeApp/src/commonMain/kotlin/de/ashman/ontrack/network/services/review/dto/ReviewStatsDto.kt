package de.ashman.ontrack.network.services.review.dto

import de.ashman.ontrack.domain.review.ReviewStats
import kotlinx.serialization.Serializable

@Serializable
data class ReviewStatsDto(
    val count: Int,
    val average: Double,
)

fun ReviewStatsDto.toDomain() = ReviewStats(
    count = count,
    average = average,
)