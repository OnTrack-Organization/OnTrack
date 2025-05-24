package de.ashman.ontrack.domain.review

import de.ashman.ontrack.database.review.ReviewEntity

data class Review(
    val id: String,
    val trackingId: String,
    val rating: Double,
    val title: String?,
    val description: String?,
    val timestamp: Long,
)

fun Review.toEntity() = ReviewEntity(
    id = id,
    trackingId = trackingId,
    rating = rating,
    title = title,
    description = description,
    timestamp = timestamp,
)