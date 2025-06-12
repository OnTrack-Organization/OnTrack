package de.ashman.ontrack.feature.notification.service

import Notification
import com.google.firebase.messaging.*
import de.ashman.ontrack.feature.notification.domain.*
import org.springframework.stereotype.Service
import com.google.firebase.messaging.Notification as FCMNotification

@Service
class PushNotificationService {

    fun sendPush(notification: Notification) {
        val fcmToken = notification.receiver.fcmToken ?: return

        val content = when (notification) {
            is FriendRequestReceived -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Friend Request",
                body = "${notification.sender.name} sent you a friend request",
                extraData = mapOf(
                    "type" to "friend_request",
                    "userId" to notification.sender.id
                )
            )

            is FriendRequestAccepted -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Friend",
                body = "${notification.sender.name} accepted your friend request",
                extraData = mapOf(
                    "type" to "friend_accept",
                    "userId" to notification.sender.id
                )
            )

            is RecommendationReceived -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Recommendation",
                body = "${notification.sender.name} recommended you ${notification.media.title}",
                extraData = mapOf(
                    "type" to "recommendation",
                    "mediaId" to notification.media.id.orEmpty(),
                    "mediaTitle" to notification.media.title.orEmpty(),
                    "mediaCoverUrl" to notification.media.coverUrl.orEmpty(),
                    "mediaType" to notification.media.type?.name.orEmpty(),
                )
            )

            is PostLiked -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Like",
                body = "${notification.sender.name} liked your post of ${notification.post.tracking.media.title}",
                extraData = mapOf(
                    "type" to "post_liked",
                    "postId" to notification.post.id.toString(),
                )
            )

            is PostCommented -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Comment",
                body = "${notification.sender.name} commented on your post of ${notification.post.tracking.media.title}",
                extraData = mapOf(
                    "type" to "post_commented",
                    "postId" to notification.post.id.toString(),
                )
            )

            is PostMentioned -> PushNotificationData(
                fcmToken = fcmToken,
                title = "New Mention",
                body = "${notification.sender.name} mentioned you in a comment of ${notification.post.tracking.media.title}",
                extraData = mapOf(
                    "type" to "post_mentioned",
                    "postId" to notification.post.id.toString(),
                )
            )

            else -> return
        }


        sendNotification(data = content)
    }

    private fun sendNotification(data: PushNotificationData) {
        val messageBuilder = Message.builder()
            .setToken(data.fcmToken)
            .putData("title", data.title)
            .putData("body", data.body)

        // Add type and related payload
        data.extraData.forEach { (key, value) ->
            messageBuilder.putData(key, value)
        }

        val message = messageBuilder
            .setNotification(
                FCMNotification.builder()
                    .setTitle(data.title)
                    .setBody(data.body)
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setTitle(data.title)
                            .setBody(data.body)
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
                    .putHeader("apns-push-type", "alert")
                    .putHeader("apns-priority", "10")
                    .putHeader("apns-topic", "your.bundle.id")
                    .build()
            )
            .build()

        FirebaseMessaging.getInstance().send(message)
    }
}
