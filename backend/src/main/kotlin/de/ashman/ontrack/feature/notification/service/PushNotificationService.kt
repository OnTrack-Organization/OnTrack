package de.ashman.ontrack.feature.notification.service

import Notification
import com.google.firebase.messaging.*
import de.ashman.ontrack.feature.notification.domain.*
import org.springframework.stereotype.Service

@Service
class PushNotificationService {

    fun sendPush(notification: Notification) {
        val fcmToken = notification.receiver.fcmToken ?: return

        val content = when (notification) {
            is FriendRequestReceived -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Friend Request",
                body = "${notification.sender.name} sent you a friend request"
            )

            is FriendRequestAccepted -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Friend",
                body = "${notification.sender.name} accepted your friend request"
            )

            is RecommendationReceived -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Recommendation",
                body = "${notification.sender.name} recommended you ${notification.recommendation.media.title}"
            )

            is PostLiked -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Like",
                body = "${notification.sender.name} liked your post of ${notification.post.tracking.media.title}"
            )

            is PostCommented -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Comment",
                body = "${notification.sender.name} commented on your post of ${notification.post.tracking.media.title}"
            )

            is PostMentioned -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Mention",
                body = "${notification.sender.name} mentioned you in a comment of ${notification.post.tracking.media.title}"
            )

            else -> return
        }

        sendNotification(content)
    }

    private fun sendNotification(data: PushNotificationData) {
        val message = Message.builder()
            .setToken(data.fcmToken)
            .setNotification(
                com.google.firebase.messaging.Notification.builder()
                    .setTitle(data.title)
                    .setBody(data.body)
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder().build()
                    )
                    .build()
            )
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(
                        Aps.builder()
                            .setMutableContent(true)
                            .build()
                    )
                    .putHeader("apns-push-type", "alert")
                    .putHeader("apns-priority", "10")
                    .putHeader("apns-topic", "your.bundle.id")
                    .build()
            )
            .build()

        FirebaseMessaging.getInstance().send(message)
    }
}
