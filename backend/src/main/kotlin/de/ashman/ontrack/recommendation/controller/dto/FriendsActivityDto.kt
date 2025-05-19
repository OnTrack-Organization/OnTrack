package de.ashman.ontrack.recommendation.controller.dto

data class FriendsActivityDto(
    val recommendations: List<RecommendationDto>,
    val trackings: List<SimpleTrackingDto>,
)