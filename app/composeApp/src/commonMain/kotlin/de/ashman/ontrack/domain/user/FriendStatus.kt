package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

@Serializable
enum class FriendStatus {
    FRIEND,
    STRANGER,
    REQUEST_RECEIVED,
    REQUEST_SENT,
}