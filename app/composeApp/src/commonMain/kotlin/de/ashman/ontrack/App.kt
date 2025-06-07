package de.ashman.ontrack

import androidx.compose.runtime.Composable
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
import de.ashman.ontrack.navigation.MediaNavigationParam
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

        startNotificationManager(
            navigate = { navController.navigate(it) }
        )
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader
        .Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .logger(DebugLogger())
        .build()

private fun startNotificationManager(
    navigate: (Route) -> Unit,
) {
    notificationInit()

    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
            Logger.d("Push received: title=$title, body=$body, data=$data")
        }

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            Logger.d("Notification clicked: payloadData: $data")

            val notificationType = data["type"].toString()

            when (notificationType) {
                "post_liked", "post_commented", "post_commented" -> {
                    val postId = data["postId"].toString()
                    navigate(Route.PostDetail(postId))
                }

                "recommendation" -> {
                    val mediaNav = MediaNavigationParam(
                        id = data["mediaId"].toString(),
                        title = data["mediaTitle"].toString(),
                        coverUrl = data["mediaCoverUrl"].toString(),
                        type = MediaType.fromStringOrThrow(data["mediaType"].toString()),
                    )
                    navigate(Route.Detail(mediaNav))
                }

                "friend_request", "friend_accept" -> {
                    val userId = data["userId"].toString()
                    navigate(Route.OtherShelf(userId))
                }

                else -> {
                    Logger.w("Unhandled notification type: $notificationType")
                }
            }
        }
    })
}
