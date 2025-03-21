package de.ashman.ontrack.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val fcmToken: String,
    val name: String,
    val username: String,
    val email: String,
    val imageUrl: String,
    val updatedAt: String,
)
