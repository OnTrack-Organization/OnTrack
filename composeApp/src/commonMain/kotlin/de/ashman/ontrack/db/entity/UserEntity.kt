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

    val friends: List<FriendEntity> = emptyList(),
    val receivedRequests: List<FriendRequestEntity> = emptyList(),
    val sentRequests: List<FriendRequestEntity> = emptyList(),
    val createdAt: Long = System.now().toEpochMilliseconds(),
)