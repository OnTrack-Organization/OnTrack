package de.ashman.ontrack.feature.review.controller.dto

import de.ashman.ontrack.feature.review.domain.Review
import java.time.ZoneOffset
import java.util.UUID

data class ReviewDto(
    val id: UUID,
    val trackingId: UUID,
    val rating: Double,
    val title: String?,
    val description: String?,
    val timestamp: Long,
)

fun Review.toDto() = ReviewDto(
    id = id,
    trackingId = trackingId,
    rating = rating,
    title = title,
    description = description,
    timestamp = updatedAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
)