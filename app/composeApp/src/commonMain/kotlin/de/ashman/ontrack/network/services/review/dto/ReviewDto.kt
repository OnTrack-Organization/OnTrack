package de.ashman.ontrack.network.services.review.dto

import de.ashman.ontrack.domain.review.Review
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: String,
    val trackingId: String,
    val rating: Double,
    val title: String?,
    val description: String?,
    val timestamp: Long,
)

fun ReviewDto.toDomain() = Review(
    id = id,
    trackingId = trackingId,
    rating = rating,
    title = title,
    description = description,
    timestamp = timestamp,
)