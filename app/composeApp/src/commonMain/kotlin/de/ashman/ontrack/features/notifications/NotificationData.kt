package de.ashman.ontrack.features.notifications

import de.ashman.ontrack.domain.media.MediaType

// TODO THIS IS GONNA NEED A REHAUL
// TODO maybe nur trackingId und dann den rest laden?
data class NotificationData(
    val type: NotificationType,

    val senderUserId: String,
    val senderUserName: String,
    val senderUserImageUrl: String,

    val repliedToUserName: String?,

    val trackingId: String,
    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaImageUrl: String?,

    val timestamp: Long,
)

enum class NotificationType {
    LIKE,
    COMMENT,
    REPLY,
    RECOMMENDATION,
}