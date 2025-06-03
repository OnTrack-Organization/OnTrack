package de.ashman.ontrack.features.settings

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.ImagePicker
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.OnTrackUsernameTextField
import de.ashman.ontrack.features.common.RemoveSheet
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.init.start.ApiContributions
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.app_version
import ontrack.composeapp.generated.resources.save_button
import ontrack.composeapp.generated.resources.settings_email_hint
import ontrack.composeapp.generated.resources.settings_logout
import ontrack.composeapp.generated.resources.settings_name_hint
import ontrack.composeapp.generated.resources.settings_remove_account
import ontrack.composeapp.generated.resources.settings_remove_confirm_text
import ontrack.composeapp.generated.resources.settings_remove_confirm_title
import ontrack.composeapp.generated.resources.settings_title
import ontrack.composeapp.generated.resources.settings_username_hint
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    commonUiManager: CommonUiManager,
    onBack: () -> Unit,
    clearAndNavigateToStart: () -> Unit,
) {
    val commonUiState by commonUiManager.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(commonUiState.snackbarMessage) {
        commonUiState.snackbarMessage?.getContentIfNotHandled()?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.user) {
        uiState.user?.let {
            localFocusManager.clearFocus()
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
                titleIcon = Icons.Default.Settings,
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
                        profilePictureUrl = uiState.imageUrl,
                        imageUploadState = uiState.imageUploadState,
                        onProfilePictureSelected = viewModel::onImagePicked,
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
                        enabled = true,
                    )
                    OnTrackUsernameTextField(
                        placeholder = stringResource(Res.string.settings_email_hint),
                        support = Res.string.settings_email_hint,
                        value = uiState.email,
                        onValueChange = {},
                        enabled = false,
                    )
                    OnTrackButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = Res.string.save_button,
                        icon = Icons.Default.Save,
                        onClick = viewModel::onSave,
                        enabled = true
                    )

                    OnTrackOutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = Res.string.settings_logout,
                        icon = Icons.AutoMirrored.Default.Logout,
                        onClick = {
                            viewModel.signOut(clearAndNavigateToStart = clearAndNavigateToStart)
                        },
                    )
                    OnTrackOutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = Res.string.settings_remove_account,
                        icon = Icons.Default.Delete,
                        onClick = { commonUiManager.showSheet() },
                    )

                    ApiContributions()
                    AppVersion()
                }
            }
        }

        if (commonUiState.showSheet) {
            ModalBottomSheet(
                onDismissRequest = { commonUiManager.hideSheet() },
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
                        isDeleting = uiState.isLoading,
                        onConfirm = {
                            viewModel.deleteAccount(clearAndNavigateToStart = clearAndNavigateToStart)
                        },
                        onCancel = { commonUiManager.hideSheet() },
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