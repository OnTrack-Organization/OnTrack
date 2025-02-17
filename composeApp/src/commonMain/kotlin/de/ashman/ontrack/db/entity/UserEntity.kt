package de.ashman.ontrack.db.entity

import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val displayName: String,
    val username: String,
    val email: String,
    val imageUrl: String,
    val createdAt: Long = System.now().toEpochMilliseconds(),
)