package de.ashman.ontrack.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual fun notificationInit() {
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            showPushNotification = true,
            notificationIconResId = 1,
        )
    )
}