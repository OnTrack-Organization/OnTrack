package de.ashman.ontrack.feature.notification.domain

import Notification
import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.share.domain.Comment
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

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
@DiscriminatorValue("RECOMMENDATION")
class RecommendationReceived(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val recommendation: Recommendation
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("POST_LIKED")
class PostLiked(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val post: Post
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("POST_COMMENTED")
class PostCommented(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    val comment: Comment
) : Notification(sender = sender, receiver = receiver)

@Entity
@DiscriminatorValue("POST_MENTIONED")
class Mentioned(
    sender: User,
    receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    val comment: Comment
) : Notification(sender = sender, receiver = receiver)
