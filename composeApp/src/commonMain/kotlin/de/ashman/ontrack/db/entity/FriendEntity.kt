package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.user.FriendRequestStatus
import kotlinx.serialization.Serializable

@Serializable
data class FriendEntity(
    val id: String,
    val username: String,
    val name: String,
    val imageUrl: String,
)

@Serializable
data class FriendRequestEntity(
    val id: String,
    val senderId: String,
    val senderUsername: String,
    val senderName: String,
    val senderImageUrl: String,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
)
