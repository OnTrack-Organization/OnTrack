package de.ashman.ontrack

import androidx.compose.runtime.Composable
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import de.ashman.ontrack.navigation.NavigationGraph
import de.ashman.ontrack.theme.OnTrackTheme

@Composable
fun App() {
    GoogleAuthProvider.create(GoogleAuthCredentials(BuildKonfig.GOOGLE_AUTH_CLIENT_ID))

    OnTrackTheme {
        NavigationGraph()
    }
}
