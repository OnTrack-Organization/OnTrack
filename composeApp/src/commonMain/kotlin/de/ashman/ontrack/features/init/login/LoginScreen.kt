package de.ashman.ontrack.features.init.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import de.ashman.ontrack.authentication.AuthViewModel
import de.ashman.ontrack.domain.user.toDomain
import de.ashman.ontrack.features.common.OnTrackButton
import dev.gitlive.firebase.auth.FirebaseUser
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.apple
import ontrack.composeapp.generated.resources.apple_login_button
import ontrack.composeapp.generated.resources.google
import ontrack.composeapp.generated.resources.google_login_button
import ontrack.composeapp.generated.resources.login_title
import ontrack.composeapp.generated.resources.or_divider
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    onNavigateAfterLogin: () -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.login_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        Column(
            modifier = modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {

            //MailLogin()

            GoogleAppleLogin(
                onClickContinue = { firebaseUser ->
                    viewModel.signUp(firebaseUser.toDomain())
                    onNavigateAfterLogin()
                }
            )
        }
    }
}

@Composable
fun MailLogin() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TODO add
    }
}

@Composable
fun GoogleAppleLogin(
    onClickContinue: (FirebaseUser) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GoogleButtonUiContainerFirebase(
            linkAccount = false,
            onResult = { result ->
                result.getOrNull()?.let(onClickContinue)
                    ?: Logger.e("Error while logging in: ${result.exceptionOrNull()?.message}")
            }
        ) {
            OnTrackButton(
                text = Res.string.google_login_button,
                icon = vectorResource(Res.drawable.google),
                onClick = { onClick() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(Res.string.or_divider),
                style = MaterialTheme.typography.bodyMedium,
                color = DividerDefaults.color,
                fontWeight = FontWeight.Bold,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        AppleButtonUiContainer(
            linkAccount = false,
            onResult = { result ->
                result.getOrNull()?.let(onClickContinue)
                    ?: Logger.e("Error while logging in: ${result.exceptionOrNull()?.message}")
            }
        ) {
            OnTrackButton(
                text = Res.string.apple_login_button,
                icon = vectorResource(Res.drawable.apple),
                onClick = { onClick() }
            )
        }
    }
}