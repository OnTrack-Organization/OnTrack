package de.ashman.ontrack.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import de.ashman.ontrack.R

actual fun notificationInit() {
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            showPushNotification = true,
            notificationIconResId = R.mipmap.ic_launcher,
        )
    )
}