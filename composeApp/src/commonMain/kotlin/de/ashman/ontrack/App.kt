package de.ashman.ontrack

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import de.ashman.ontrack.navigation.NavigationGraph
import de.ashman.ontrack.theme.OnTrackTheme

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    GoogleAuthProvider.create(GoogleAuthCredentials(BuildKonfig.GOOGLE_AUTH_CLIENT_ID))

    OnTrackTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        NavigationGraph()
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader
        .Builder(context)
        .crossfade(true)
        .logger(DebugLogger())
        .build()