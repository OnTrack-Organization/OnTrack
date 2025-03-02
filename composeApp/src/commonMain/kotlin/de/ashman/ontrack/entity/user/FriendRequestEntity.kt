package de.ashman.ontrack.entity.user

import de.ashman.ontrack.domain.user.FriendRequestStatus
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestEntity(
    val userId: String,
    val username: String,
    val name: String,
    val imageUrl: String,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
)
