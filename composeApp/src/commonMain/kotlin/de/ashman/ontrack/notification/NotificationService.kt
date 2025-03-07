package de.ashman.ontrack.notification

import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.AuthRepository
import dev.gitlive.firebase.functions.FirebaseFunctions

interface NotificationService {
    suspend fun sendPushNotification(
        userId: String,
        title: String,
        body: String,
        mediaId: String? = null,
        imageUrl: String? = null
    )
}

class NotificationServiceImpl(
    private val functions: FirebaseFunctions,
    private val authRepository: AuthRepository,
) : NotificationService {
    override suspend fun sendPushNotification(userId: String, title: String, body: String, mediaId: String?, imageUrl: String?) {
        // Only send notification to other users
        if (authRepository.currentUserId != userId) {
            val data = hashMapOf(
                "userId" to userId,
                "title" to title,
                "body" to body,
                "mediaId" to mediaId,
                "image" to imageUrl,
            )

            Logger.d("Sending notification to user: $userId")
            Logger.d("Notification data: $data")

            try {
                val result = functions
                    .httpsCallable("sendPushNotification")
                    .invoke(data)

                Logger.d("Notification sent successfully: $result")
            } catch (e: Exception) {
                Logger.e("Error sending notification: ${e.message}")
            }
        }
    }
}
