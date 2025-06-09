package de.ashman.ontrack.domain.user

import de.ashman.ontrack.domain.tracking.Tracking

data class UserProfile(
    val user: OtherUser,
    val trackings: List<Tracking>,
    val blocked: Boolean,
)