package de.ashman.ontrack.features.init.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.SharedUiManager
import kotlinx.coroutines.launch
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.apple
import ontrack.app.composeapp.generated.resources.apple_login_button
import ontrack.app.composeapp.generated.resources.google
import ontrack.app.composeapp.generated.resources.google_login_button
import ontrack.app.composeapp.generated.resources.login_title
import ontrack.app.composeapp.generated.resources.or_divider
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    sharedUiManager: SharedUiManager,
    onNavigateAfterLogin: (Boolean, User?) -> Unit,
    onBack: () -> Unit,
) {
    val sharedUiState by sharedUiManager.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(sharedUiState.snackbarMessage) {
        sharedUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.login_title),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                onClickNavigation = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        Column(
            modifier = modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {

            GoogleAppleLogin(
                onClickContinue = { result ->
                    coroutineScope.launch {
                        val userExist = viewModel.doesUserExist(result.getOrNull()?.id)

                        viewModel.signIn(
                            loginResult = result,
                            onSuccess = { user -> onNavigateAfterLogin(userExist, user) }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun GoogleAppleLogin(
    onClickContinue: (Result<User?>) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GoogleButtonUiContainerFirebase(
            linkAccount = false,
            onResult = { result ->
                val userResult = result.mapCatching { it?.toDomain() }
                onClickContinue(userResult)
            }
        ) {
            OnTrackButton(
                modifier = Modifier.fillMaxWidth(),
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
                val userResult = result.mapCatching { it?.toDomain() }
                onClickContinue(userResult)
            }
        ) {
            OnTrackButton(
                modifier = Modifier.fillMaxWidth(),
                text = Res.string.apple_login_button,
                icon = vectorResource(Res.drawable.apple),
                onClick = { onClick() }
            )
        }
    }
}