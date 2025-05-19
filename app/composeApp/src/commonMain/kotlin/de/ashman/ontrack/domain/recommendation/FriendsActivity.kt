package de.ashman.ontrack.domain.recommendation

data class FriendsActivity(
    val recommendations: List<NewRecommendation>,
    val trackings: List<SimpleTracking>,
)