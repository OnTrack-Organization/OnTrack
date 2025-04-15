package de.ashman.ontrack.domain.newdomains

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    val id: String,
    val username: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String,
)