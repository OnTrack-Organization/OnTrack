package de.ashman.ontrack.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.google_login
import ontrack.composeapp.generated.resources.welcome
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.welcome),
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.size(16.dp))

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
            GoogleSignInButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.google_login),
            ) { onClick() }
        }
    }
}
