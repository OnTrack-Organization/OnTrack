package de.ashman.ontrack.network.services.notification.dto

import de.ashman.ontrack.domain.notification.FriendRequestAccepted
import de.ashman.ontrack.domain.notification.FriendRequestReceived
import de.ashman.ontrack.domain.notification.PostMentioned
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.domain.notification.PostCommented
import de.ashman.ontrack.domain.notification.PostLiked
import de.ashman.ontrack.domain.notification.RecommendationReceived
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class NotificationDto {
    abstract val id: String
    abstract val sender: UserDto
    abstract val read: Boolean
    abstract val timestamp: Long
}

fun NotificationDto.toDomain(): Notification = when (this) {
    is FriendRequestReceivedDto -> FriendRequestReceived(
        id = id,
        sender = sender.toDomain(),
        read = read,
        timestamp = timestamp
    )

    is FriendRequestAcceptedDto -> FriendRequestAccepted(
        id = id,
        sender = sender.toDomain(),
        read = read,
        timestamp = timestamp
    )

    is RecommendationReceivedDto -> RecommendationReceived(
        id = id,
        sender = sender.toDomain(),
        read = read,
        timestamp = timestamp,
        recommendation = recommendation.toDomain()
    )

    is PostLikedDto -> PostLiked(
        id = id,
        sender = sender.toDomain(),
        read = read,
        timestamp = timestamp,
        post = post.toDomain()
    )

    is PostCommentedDto -> PostCommented(
        id = id,
        sender = sender.toDomain(),
        read = read,
        timestamp = timestamp,
        post = post.toDomain(),
        comment = comment.toDomain()
    )

    is MentionedDto -> PostMentioned(
        id = id,
        sender = sender.toDomain(),
        read = read,
        timestamp = timestamp,
        post = post.toDomain(),
        comment = comment.toDomain()
    )
}

