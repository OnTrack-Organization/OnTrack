package de.ashman.ontrack.feature.notification.controller.dto

import Notification
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.ashman.ontrack.feature.notification.domain.*
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import java.util.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FriendRequestReceivedDto::class, name = "FriendRequestReceivedDto"),
    JsonSubTypes.Type(value = FriendRequestAcceptedDto::class, name = "FriendRequestAcceptedDto"),
    JsonSubTypes.Type(value = RecommendationReceivedDto::class, name = "RecommendationReceivedDto"),
    JsonSubTypes.Type(value = PostLikedDto::class, name = "PostLikedDto"),
    JsonSubTypes.Type(value = PostCommentedDto::class, name = "PostCommentedDto"),
    JsonSubTypes.Type(value = MentionedDto::class, name = "MentionedDto")
)
sealed class NotificationDto {
    abstract val id: UUID
    abstract val sender: UserDto
    abstract val read: Boolean
    abstract val timestamp: Long
}

fun Notification.toDto(): NotificationDto = when (this) {
    is FriendRequestReceived -> FriendRequestReceivedDto(
        id = id,
        sender = sender.toDto(),
        read = read,
        timestamp = updatedAt.toEpochMilli(),
    )

    is FriendRequestAccepted -> FriendRequestAcceptedDto(
        id = id,
        sender = sender.toDto(),
        read = read,
        timestamp = updatedAt.toEpochMilli(),
    )

    is RecommendationReceived -> RecommendationReceivedDto(
        id = id,
        sender = sender.toDto(),
        read = read,
        media = media.toDto(),
        timestamp = updatedAt.toEpochMilli(),
    )

    is PostLiked -> PostLikedDto(
        id = id,
        sender = sender.toDto(),
        read = read,
        post = post.toSimpleDto(),
        timestamp = updatedAt.toEpochMilli(),
    )

    is PostCommented -> PostCommentedDto(
        id = id,
        sender = sender.toDto(),
        read = read,
        post = post.toSimpleDto(),
        timestamp = updatedAt.toEpochMilli(),
    )

    is PostMentioned -> MentionedDto(
        id = id,
        sender = sender.toDto(),
        read = read,
        post = post.toSimpleDto(),
        timestamp = updatedAt.toEpochMilli(),
    )

    else -> throw IllegalArgumentException("Unknown notification type: $this")
}
