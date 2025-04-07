package de.ashman.ontrack.notification

import co.touchlab.kermit.Logger
import de.ashman.ontrack.datastore.UserDataStore
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
    private val userDataStore: UserDataStore,
) : NotificationService {
    override suspend fun sendPushNotification(userId: String, title: String, body: String, mediaId: String?, imageUrl: String?) {
        // Only send notification to other users
        if (userDataStore.getCurrentUserId() != userId) {
            val data = hashMapOf(
                "userId" to userId,
                "title" to title,
                "body" to body,
                "mediaId" to mediaId,
                "image" to imageUrl,
            )

            Logger.d("Sending notification to user: $userId", tag = TAG)
            Logger.d("Notification data: $data", tag = TAG)

            try {
                val result = functions
                    .httpsCallable("sendPushNotification")
                    .invoke(data)

                Logger.d("Notification sent successfully: $result", tag = TAG)
            } catch (e: Exception) {
                Logger.e("Error sending notification: ${e.message}", tag = TAG)
            }
        }
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
