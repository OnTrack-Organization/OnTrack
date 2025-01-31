package de.ashman.ontrack.authentication.user

import kotlinx.serialization.Serializable

data class User(
    val id: String,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
)

@Serializable
data class UserEntity(
    val id: String,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
)