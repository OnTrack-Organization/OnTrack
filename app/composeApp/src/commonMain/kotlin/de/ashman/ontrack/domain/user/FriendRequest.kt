package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val userId: String,
    val username: String,
    val name: String,
    val imageUrl: String,
)
