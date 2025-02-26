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
import androidx.compose.material3.Button
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
import de.ashman.ontrack.features.common.OnTrackUserTextField
import de.ashman.ontrack.features.common.RemoveSheetContent
import de.ashman.ontrack.features.init.start.ApiContributions
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.save_button
import ontrack.composeapp.generated.resources.settings_logout
import ontrack.composeapp.generated.resources.settings_name_hint
import ontrack.composeapp.generated.resources.settings_remove
import ontrack.composeapp.generated.resources.settings_remove_confirm_text
import ontrack.composeapp.generated.resources.settings_remove_confirm_title
import ontrack.composeapp.generated.resources.settings_title
import ontrack.composeapp.generated.resources.settings_username_empty
import ontrack.composeapp.generated.resources.settings_username_hint
import ontrack.composeapp.generated.resources.settings_username_taken
import ontrack.composeapp.generated.resources.settings_username_too_long
import ontrack.composeapp.generated.resources.settings_username_too_short
import org.jetbrains.compose.resources.StringResource
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
                    OnTrackUserTextField(
                        placeholder = "Name",
                        support = Res.string.settings_name_hint,
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                    )
                    OnTrackUserTextField(
                        placeholder = "Username",
                        support = Res.string.settings_username_hint,
                        value = uiState.username,
                        errorSupport = uiState.usernameError?.getLabel(),
                        onValueChange = viewModel::onUsernameChange,
                    )
                    OnTrackButton(
                        text = Res.string.save_button,
                        icon = Icons.Default.Save,
                        onClick = viewModel::onUpdateUser,
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
                    RemoveSheetContent(
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

@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
) {
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Single,
    ) { file ->
        // TODO somehow save the image to the db?
        // Handle the picked files
    }

    Button(
        modifier = modifier,
        onClick = { launcher.launch() },
    ) {
        Text(text = "Pick an image")
    }
}

fun UsernameError.getLabel(): StringResource {
    return when (this) {
        UsernameError.EMPTY -> Res.string.settings_username_empty
        UsernameError.TAKEN -> Res.string.settings_username_taken
        UsernameError.TOO_LONG -> Res.string.settings_username_too_long
        UsernameError.TOO_SHORT -> Res.string.settings_username_too_short
    }
}