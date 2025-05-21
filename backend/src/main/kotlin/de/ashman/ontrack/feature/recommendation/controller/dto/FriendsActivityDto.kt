package de.ashman.ontrack.feature.recommendation.controller.dto

data class FriendsActivityDto(
    val recommendations: List<RecommendationDto>,
    val trackings: List<SimpleTrackingDto>,
)