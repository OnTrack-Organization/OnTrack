package de.ashman.ontrack.feature.notification.domain

data class PushNotificationData(
    val fcmToken: String,
    val title: String,
    val body: String,
    val extraData: Map<String, String> = emptyMap(),
)