package de.ashman.ontrack.login.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val friends: Map<String, Boolean> = emptyMap(),  // Friends stored as a map of user IDs
    val reviews: Map<String, Boolean> = emptyMap()  // Reviews stored as a map of review IDs
)
