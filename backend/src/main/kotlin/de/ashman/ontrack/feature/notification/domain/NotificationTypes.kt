package de.ashman.ontrack.feature.notification.domain

import Notification
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.tracking.domain.Media
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@DiscriminatorValue("FRIEND_REQUEST_RECEIVED")
class FriendRequestReceived(
    sender: User,
    receiver: User
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("FRIEND_REQUEST_ACCEPTED")
class FriendRequestAccepted(
    sender: User,
    receiver: User
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("RECOMMENDATION_RECEIVED")
class RecommendationReceived(
    sender: User,
    receiver: User,

    @Embedded
    val media: Media
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("POST_LIKED")
class PostLiked(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val post: Post
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("POST_COMMENTED")
class PostCommented(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val post: Post,
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("POST_MENTIONED")
class PostMentioned(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val post: Post,
) : Notification(sender = sender, receiver = receiver)
