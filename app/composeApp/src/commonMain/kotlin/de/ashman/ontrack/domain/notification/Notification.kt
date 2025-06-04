package de.ashman.ontrack.domain.notification

import de.ashman.ontrack.domain.user.User

sealed class Notification {
    abstract val id: String
    abstract val sender: User
    abstract val read: Boolean
    abstract val timestamp: Long
}

data class FriendRequestReceived(
    override val id: String,
    override val sender: User,
    override val read: Boolean,
    override val timestamp: Long
) : Notification()

data class FriendRequestAccepted(
    override val id: String,
    override val sender: User,
    override val read: Boolean,
    override val timestamp: Long
) : Notification()

data class RecommendationReceived(
    override val id: String,
    override val sender: User,
    override val read: Boolean,
    override val timestamp: Long,
    val recommendation: SimpleRecommendation
) : Notification()

data class PostLiked(
    override val id: String,
    override val sender: User,
    override val read: Boolean,
    override val timestamp: Long,
    val post: SimplePost
) : Notification()

data class PostCommented(
    override val id: String,
    override val sender: User,
    override val read: Boolean,
    override val timestamp: Long,
    val post: SimplePost,
    val comment: SimpleComment
) : Notification()

data class PostMentioned(
    override val id: String,
    override val sender: User,
    override val read: Boolean,
    override val timestamp: Long,
    val post: SimplePost,
    val comment: SimpleComment
) : Notification()
