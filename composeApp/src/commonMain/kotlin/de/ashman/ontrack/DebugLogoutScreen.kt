package de.ashman.ontrack

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.authentication.AuthViewModel

@Composable
fun DebugLogoutScreen(
    viewModel: AuthViewModel,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.user != null) {
        Column(
            modifier = modifier,
        ) {
            Text("Welcome, ${uiState.user?.name}")

            Button(
                onClick = {
                    viewModel.signOut()
                    onClickLogout()
                }) {
                Text("Logout")
            }

            Button(
                onClick = {
                    viewModel.deleteAccount()
                    onClickLogout()
                }) {
                Text("Delete account")
            }
        }
    } else {
        Text("Login bro")
    }
}