package de.ashman.ontrack

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import de.ashman.ontrack.navigation.NavigationGraph
import de.ashman.ontrack.notification.notificationInit
import de.ashman.ontrack.theme.OnTrackTheme

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    OnTrackTheme {
        GoogleAuthProvider.create(GoogleAuthCredentials(BuildKonfig.GOOGLE_AUTH_CLIENT_ID))

        notificationInit()
        //startNotificationManager()

        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        NavigationGraph()
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader
        .Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        /* .memoryCache {
             MemoryCache
                 .Builder()
                 .maxSizePercent(context, 0.3)
                 .strongReferencesEnabled(true)
                 .build()
         }
         .diskCachePolicy(CachePolicy.ENABLED)
         .diskCache {
             DiskCache.Builder()
                 .maxSizePercent(0.02)
                 .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                 .build()
         }*/
        .logger(DebugLogger())
        .build()

private fun startNotificationManager() {
    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onPushNotification(title: String?, body: String?) {
            super.onPushNotification(title, body)
            println("Push Notification notification type message is received: Title: $title and Body: $body")
        }

        override fun onPayloadData(data: PayloadData) {
            super.onPayloadData(data)
            println("Push Notification payloadData: $data")
        }

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            println("Notification clicked, Notification payloadData: $data")
        }
    }
    )
}
