package de.ashman.ontrack.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Welcome to OnTrack!",
            style = MaterialTheme.typography.titleLarge,
        )

        GoogleButtonUiContainerFirebase(
            linkAccount = false,
            onResult = { result ->
                result.fold(
                    onSuccess = {
                        val firebaseUser = result.getOrNull()

                        if (firebaseUser != null) {
                            authViewModel.signUp(firebaseUser)
                            onLoginSuccess()
                        }
                    },
                    onFailure = { Logger.e("Error while logging in: ${it.message}") }
                )
            }
        ) {
            GoogleSignInButton(modifier = Modifier.fillMaxWidth()) { onClick() }
        }
    }
}
