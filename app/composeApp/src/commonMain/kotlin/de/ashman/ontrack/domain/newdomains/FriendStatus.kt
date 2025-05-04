package de.ashman.ontrack.domain.newdomains

import kotlinx.serialization.Serializable

@Serializable
enum class FriendStatus {
    FRIEND,
    STRANGER,
    REQUEST_RECEIVED,
    REQUEST_SENT,
}