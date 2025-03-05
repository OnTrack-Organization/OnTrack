package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val fcmToken: String,
    val name: String,
    val username: String,
    val email: String,
    val imageUrl: String,
)
