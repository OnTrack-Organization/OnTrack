package de.ashman.ontrack

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
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
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.navigation.Route
import de.ashman.ontrack.navigation.graph.NavigationGraph
import de.ashman.ontrack.notification.notificationInit
import de.ashman.ontrack.theme.OnTrackTheme

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    OnTrackTheme {
        val navController = rememberNavController()
        GoogleAuthProvider.create(GoogleAuthCredentials(BuildKonfig.GOOGLE_AUTH_CLIENT_ID))
        setSingletonImageLoaderFactory { context -> getAsyncImageLoader(context) }

        NavigationGraph(navController)

        startNotificationManager(navController)
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader
        .Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .logger(DebugLogger())
        .build()

private fun startNotificationManager(
    navController: NavController
) {
    val TAG = "NotificationManager"
    notificationInit()

    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
            Logger.d(
                "Push Notification is received: " +
                        "Title: $title and " +
                        "Body: $body and " +
                        "Notification payloadData: $data"
            )
        }

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            Logger.d("Notification clicked, Notification payloadData: $data", tag = TAG)

            val mediaNav = MediaNavigationItems(
                id = data["mediaId"].toString(),
                title = "",
                coverUrl = "",
                mediaType = MediaType.MOVIE,
            )

            try {
                val route = Route.Detail(mediaNav)
                Logger.d("Route: $route", tag = TAG)
                navController.navigate(route)
            } catch (e: Exception) {
                Logger.e("Failed to navigate: ${e.message}", tag = TAG)
            }
        }
    })
}
