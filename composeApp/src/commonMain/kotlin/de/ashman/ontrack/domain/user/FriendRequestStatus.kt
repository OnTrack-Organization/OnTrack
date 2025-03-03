package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

@Serializable
enum class FriendRequestStatus {
    PENDING, ACCEPTED, DECLINED, CANCELLED
}