package de.ashman.ontrack.domain.recommendation

data class FriendsActivity(
    val recommendations: List<Recommendation>,
    val trackings: List<SimpleTracking>,
)