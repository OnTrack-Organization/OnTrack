package de.ashman.ontrack.features.settings

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.features.common.ImagePicker
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.OnTrackUsernameTextField
import de.ashman.ontrack.features.common.RemoveSheet
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.init.start.ApiContributions
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.app_version
import ontrack.composeapp.generated.resources.remove_button
import ontrack.composeapp.generated.resources.save_button
import ontrack.composeapp.generated.resources.settings_email_hint
import ontrack.composeapp.generated.resources.settings_logout
import ontrack.composeapp.generated.resources.settings_name_hint
import ontrack.composeapp.generated.resources.settings_remove_confirm_text
import ontrack.composeapp.generated.resources.settings_remove_confirm_title
import ontrack.composeapp.generated.resources.settings_title
import ontrack.composeapp.generated.resources.settings_username_hint
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    sharedUiManager: SharedUiManager,
    onBack: () -> Unit,
    clearAndNavigateOnLogout: () -> Unit,
) {
    val sharedUiState by sharedUiManager.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(sharedUiState.snackbarMessage) {
        sharedUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUnsavedChanges()
        }
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.settings_title),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                onClickNavigation = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                }
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    ImagePicker(
                        imageUrl = uiState.imageUrl,
                        onImagePicked = viewModel::onImagePicked,
                    )

                    OnTrackUsernameTextField(
                        placeholder = stringResource(Res.string.settings_name_hint),
                        support = Res.string.settings_name_hint,
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        enabled = true,
                    )
                    OnTrackUsernameTextField(
                        placeholder = stringResource(Res.string.settings_username_hint),
                        support = Res.string.settings_username_hint,
                        value = uiState.username,
                        errorSupport = uiState.usernameError?.getLabel(),
                        onValueChange = viewModel::onUsernameChange,
                        enabled = false,
                    )
                    OnTrackUsernameTextField(
                        placeholder = stringResource(Res.string.settings_email_hint),
                        support = Res.string.settings_email_hint,
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        enabled = false,
                    )
                    OnTrackButton(
                        text = Res.string.save_button,
                        icon = Icons.Default.Save,
                        onClick = viewModel::onUpdateUser,
                        enabled = true
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OnTrackOutlinedButton(
                            modifier = Modifier.weight(1f),
                            text = Res.string.settings_logout,
                            icon = Icons.AutoMirrored.Default.Logout,
                            onClick = {
                                viewModel.signOut(
                                    onSuccess = clearAndNavigateOnLogout,
                                )
                            },
                        )
                        OnTrackOutlinedButton(
                            modifier = Modifier.weight(1f),
                            text = Res.string.remove_button,
                            icon = Icons.Default.Delete,
                            onClick = { sharedUiManager.showSheet() },
                        )
                    }
                    ApiContributions()
                    AppVersion()
                }
            }
        }

        if (sharedUiState.showSheet) {
            ModalBottomSheet(
                onDismissRequest = { sharedUiManager.hideSheet() },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RemoveSheet(
                        title = Res.string.settings_remove_confirm_title,
                        text = Res.string.settings_remove_confirm_text,
                        onConfirm = {
                            viewModel.removeUser()
                            clearAndNavigateOnLogout()
                        },
                        onCancel = { sharedUiManager.hideSheet() },
                    )
                }
            }
        }
    }
}

@Composable
fun AppVersion() {
    Text(stringResource(Res.string.app_version, BuildKonfig.APP_VERSION))
}