package de.ashman.ontrack.domain.globalrating

import kotlinx.serialization.Serializable

@Serializable
data class RatingStats(
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0
)