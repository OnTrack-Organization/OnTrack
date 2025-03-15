package de.ashman.ontrack.entity.globalrating

import kotlinx.serialization.Serializable

@Serializable
data class RatingEntity(
    val userId: String,
    val rating: Double
)
