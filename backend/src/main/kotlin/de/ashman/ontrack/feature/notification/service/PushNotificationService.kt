package de.ashman.ontrack.feature.notification.service

import Notification
import com.google.firebase.messaging.*
import com.google.firebase.messaging.Notification as FCMNotification
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
                body = "${notification.sender.name} sent you a friend request",
                profilePictureUrl = notification.sender.profilePictureUrl,
            )

            is FriendRequestAccepted -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Friend",
                body = "${notification.sender.name} accepted your friend request",
                profilePictureUrl = notification.sender.profilePictureUrl,
            )

            is RecommendationReceived -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Recommendation",
                body = "${notification.sender.name} recommended you ${notification.recommendation.media.title}",
                profilePictureUrl = notification.sender.profilePictureUrl,
            )

            is PostLiked -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Like",
                body = "${notification.sender.name} liked your post of ${notification.post.tracking.media.title}",
                profilePictureUrl = notification.sender.profilePictureUrl,
            )

            is PostCommented -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Comment",
                body = "${notification.sender.name} commented on your post of ${notification.post.tracking.media.title}",
                profilePictureUrl = notification.sender.profilePictureUrl,
            )

            is PostMentioned -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Mention",
                body = "${notification.sender.name} mentioned you in a comment of ${notification.post.tracking.media.title}",
                profilePictureUrl = notification.sender.profilePictureUrl,
            )

            else -> return
        }

        sendNotification(content)
    }

    private fun sendNotification(data: PushNotificationData) {
        val message = Message.builder()
            .setToken(data.fcmToken)
            .setNotification(
                FCMNotification.builder()
                    .setTitle(data.title)
                    .setBody(data.body)
                    // Sets a large image below the body
                    //.setImage(data.profilePictureUrl)
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setTitle(data.title)
                            .setBody(data.body)
                            //.setIcon(data.profilePictureUrl)
                            //.setImage(data.profilePictureUrl)
                            .build()
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
                    //.putCustomData()
                    .putHeader("apns-push-type", "alert")
                    .putHeader("apns-priority", "10")
                    .putHeader("apns-topic", "your.bundle.id")
                    .build()
            )
            .build()

        FirebaseMessaging.getInstance().send(message)
    }
}
