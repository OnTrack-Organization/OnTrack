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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import de.ashman.ontrack.features.common.OnTrackUsernameTextField
import de.ashman.ontrack.features.common.RemoveSheet
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.init.start.ApiContributions
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.save_button
import ontrack.composeapp.generated.resources.settings_logout
import ontrack.composeapp.generated.resources.settings_name_hint
import ontrack.composeapp.generated.resources.settings_remove
import ontrack.composeapp.generated.resources.settings_remove_confirm_text
import ontrack.composeapp.generated.resources.settings_remove_confirm_title
import ontrack.composeapp.generated.resources.settings_title
import ontrack.composeapp.generated.resources.settings_username_hint
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    userId: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val localFocusManager = LocalFocusManager.current

    // TODO maybe just pass the user from the shelf screen via nav param
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) viewModel.observeUser(userId)
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            localFocusManager.clearFocus()
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUnsavedChanges()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.settings_title),
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
                ) {
                    OnTrackUsernameTextField(
                        placeholder = "Name",
                        support = Res.string.settings_name_hint,
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        enabled = true,
                    )
                    OnTrackUsernameTextField(
                        placeholder = "Username",
                        support = Res.string.settings_username_hint,
                        value = uiState.username,
                        errorSupport = uiState.usernameError?.getLabel(),
                        onValueChange = viewModel::onUsernameChange,
                        enabled = false,
                    )
                    OnTrackButton(
                        text = Res.string.save_button,
                        icon = Icons.Default.Save,
                        onClick = viewModel::onUpdateUser,
                        enabled = true
                    )
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OnTrackOutlinedButton(
                        text = Res.string.settings_logout,
                        icon = Icons.AutoMirrored.Default.Logout,
                        onClick = {
                            viewModel.signOut(
                                onSuccess = onLogout,
                            )
                        },
                    )
                    OnTrackOutlinedButton(
                        text = Res.string.settings_remove,
                        icon = Icons.Default.Delete,
                        onClick = { showBottomSheet = true },
                    )
                    ApiContributions()
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
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
                            showBottomSheet = false
                            viewModel.deleteAccount()
                            onLogout()
                        },
                        onCancel = { showBottomSheet = false },
                    )
                }
            }
        }
    }
}
