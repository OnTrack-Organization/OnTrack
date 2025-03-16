package de.ashman.ontrack.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestEntity(
    val userId: String,
    val username: String,
    val name: String,
    val imageUrl: String,
)
