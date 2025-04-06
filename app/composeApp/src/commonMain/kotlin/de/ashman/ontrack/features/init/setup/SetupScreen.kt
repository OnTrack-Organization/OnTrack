package de.ashman.ontrack.features.init.setup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.user.NewUser
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.ImagePicker
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.OnTrackUsernameTextField
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.settings.ImageUploadState
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.settings_name_hint
import ontrack.composeapp.generated.resources.settings_username_hint
import ontrack.composeapp.generated.resources.setup_title
import ontrack.composeapp.generated.resources.setup_username
import ontrack.composeapp.generated.resources.start_button
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel,
    commonUiManager: CommonUiManager,
    user: NewUser,
    navigateToSearch: () -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.preFillUserDataFromLogin(user)
    }

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(title = stringResource(Res.string.setup_title))
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .consumeWindowInsets(contentPadding)
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.setup_username),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )

                ImagePicker(
                    imageUrl = uiState.imageUrl,
                    imageUploadState = uiState.imageUploadState,
                    onImagePicked = viewModel::onImagePicked,
                )

                OnTrackUsernameTextField(
                    placeholder = "Name",
                    support = Res.string.settings_name_hint,
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                )
                OnTrackUsernameTextField(
                    placeholder = "Username",
                    support = Res.string.settings_username_hint,
                    value = uiState.username,
                    errorSupport = uiState.usernameError?.getLabel(),
                    onValueChange = viewModel::onUsernameChange,
                )

                val startButtonEnabled = uiState.name.isNotBlank() &&
                        uiState.username.isNotBlank() &&
                        uiState.usernameError == null &&
                        uiState.imageUploadState != ImageUploadState.Uploading

                OnTrackButton(
                    text = Res.string.start_button,
                    icon = Icons.Default.Celebration,
                    onClick = {
                        viewModel.onSave(navigateToSearch)
                    },
                    enabled = startButtonEnabled,
                )
            }
        }
    }
}