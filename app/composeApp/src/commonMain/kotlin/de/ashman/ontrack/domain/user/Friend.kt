package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val id: String,
    val username: String,
    val name: String,
    val imageUrl: String,
)