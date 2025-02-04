package de.ashman.ontrack.features.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.authentication.AuthViewModel

@Composable
fun FeedScreen(
    authViewModel: AuthViewModel,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.user != null) {
        Column(
            modifier = modifier,
        ) {
            Text("Welcome, ${uiState.user?.name}")

            Button(
                onClick = {
                    authViewModel.signOut()
                    onClickLogout()
                }) {
                Text("Logout")
            }

            Button(
                onClick = {
                    authViewModel.deleteAccount()
                    onClickLogout()
                }) {
                Text("Delete account")
            }
        }
    } else {
        Text("Login bro")
    }
}