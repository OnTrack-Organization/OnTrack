package de.ashman.ontrack.feature.notification.service

import Notification
import de.ashman.ontrack.feature.notification.domain.*
import de.ashman.ontrack.feature.notification.repository.NotificationRepository
import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.share.domain.Comment
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.user.domain.User
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val pushNotificationService: PushNotificationService,
) {
    fun getLatestNotificationsForUser(userId: String): List<Notification> {
        return notificationRepository.findTop50ByReceiverIdOrderByCreatedAtDesc(userId)
    }

    fun markNotificationAsRead(userId: String, notificationId: UUID) {
        notificationRepository.markAsRead(receiverId = userId, id = notificationId)
    }

    fun createFriendRequestReceived(sender: User, receiver: User) {
        val notification = FriendRequestReceived(sender = sender, receiver = receiver)
        val saved = notificationRepository.save(notification)
        pushNotificationService.sendPush(saved)
    }

    fun createFriendRequestAccepted(acceptor: User, originalSender: User) {
        val notification = FriendRequestAccepted(sender = originalSender, receiver = acceptor)
        val saved = notificationRepository.save(notification)
        pushNotificationService.sendPush(saved)
    }

    fun createRecommendationReceived(sender: User, receiver: User, recommendation: Recommendation) {
        val notification = RecommendationReceived(sender, receiver, recommendation)
        val saved = notificationRepository.save(notification)
        pushNotificationService.sendPush(saved)
    }

    fun createPostLiked(liker: User, postOwner: User, post: Post) {
        if (liker == postOwner) return

        val notification = PostLiked(liker, postOwner, post)
        val saved = notificationRepository.save(notification)
        pushNotificationService.sendPush(saved)
    }

    fun createPostCommented(commenter: User, postOwner: User, post: Post, comment: Comment) {
        if (commenter == postOwner) return

        val notification = PostCommented(commenter, postOwner, post, comment)
        val saved = notificationRepository.save(notification)
        pushNotificationService.sendPush(saved)
    }

    // TODO later
    fun createMentioned(sender: User, receiver: User, post: Post, comment: Comment) {
        if (sender == receiver) return

        val notification = Mentioned(sender, receiver, post, comment)
        val saved = notificationRepository.save(notification)
        pushNotificationService.sendPush(saved)
    }

}
