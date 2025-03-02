package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val userId: String,
    val username: String,
    val name: String,
    val imageUrl: String,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
)

@Serializable
enum class FriendRequestStatus {
    PENDING, ACCEPTED, DECLINED, CANCELLED
}