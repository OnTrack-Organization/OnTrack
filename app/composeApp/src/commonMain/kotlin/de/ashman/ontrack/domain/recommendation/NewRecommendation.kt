package de.ashman.ontrack.domain.recommendation

import de.ashman.ontrack.domain.user.User

data class NewRecommendation(
    val user: User,
    val message: String,
    val timestamp: Long,
)