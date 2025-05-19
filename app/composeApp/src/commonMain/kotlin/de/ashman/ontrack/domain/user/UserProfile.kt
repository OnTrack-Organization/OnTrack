package de.ashman.ontrack.domain.user

import de.ashman.ontrack.domain.tracking.NewTracking

data class UserProfile(
    val user: OtherUser,
    val trackings: List<NewTracking>,
)