package de.ashman.ontrack.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.on_track_icon_v1

actual fun notificationInit() {
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = true,
        )
    )
}