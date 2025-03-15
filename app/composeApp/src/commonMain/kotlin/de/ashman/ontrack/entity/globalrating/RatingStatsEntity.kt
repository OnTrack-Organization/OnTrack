package de.ashman.ontrack.entity.globalrating

import kotlinx.serialization.Serializable

@Serializable
data class RatingStatsEntity(
    val averageRating: Double,
    val ratingCount: Int,
)