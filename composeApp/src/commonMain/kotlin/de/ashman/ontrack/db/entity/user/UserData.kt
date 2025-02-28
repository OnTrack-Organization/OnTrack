package de.ashman.ontrack.db.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String,
    val name: String,
    val username: String,
    val imageUrl: String,
)