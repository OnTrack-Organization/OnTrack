package de.ashman.ontrack.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class FriendEntity(
    val id: String,
    val username: String,
    val name: String,
    val imageUrl: String,
)
