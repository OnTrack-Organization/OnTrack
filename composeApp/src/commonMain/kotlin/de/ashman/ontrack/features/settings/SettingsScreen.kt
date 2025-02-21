package de.ashman.ontrack.features.settings

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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.DeleteSheetContent
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.init.start.ApiContributions
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.settings_delete
import ontrack.composeapp.generated.resources.settings_delete_confirm_text
import ontrack.composeapp.generated.resources.settings_delete_confirm_title
import ontrack.composeapp.generated.resources.settings_logout
import ontrack.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OnTrackButton(
                        text = Res.string.settings_logout,
                        icon = Icons.AutoMirrored.Default.Logout,
                        onClick = onLogout,
                    )
                    OnTrackButton(
                        text = Res.string.settings_delete,
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
                    DeleteSheetContent(
                        title = Res.string.settings_delete_confirm_title,
                        text = Res.string.settings_delete_confirm_text,
                        onConfirm = {
                            showBottomSheet = false
                            onLogout()
                            viewModel.deleteAccount()
                        },
                        onCancel = { showBottomSheet = false },
                    )
                }
            }
        }
    }
}
