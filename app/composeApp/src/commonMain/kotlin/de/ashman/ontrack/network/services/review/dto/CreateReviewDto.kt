package de.ashman.ontrack.network.services.review.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewDto(
    val trackingId: String,
    val rating: Double,
    val title: String?,
    val description: String?,
)