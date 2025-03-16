package de.ashman.ontrack.domain.user

import kotlinx.serialization.Serializable

// TODO welche states brauchen wir?
// None (noch kein request geschickt)
// Sent (request geschickt)
// Received (request empfangen)
@Serializable
enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    CANCELLED,
    RECEIVED,
    NONE
}