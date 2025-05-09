package de.ashman.ontrack.domain.newdomains

data class UserProfile(
    val user: OtherUser,
    val trackings: List<NewTracking>,
)
