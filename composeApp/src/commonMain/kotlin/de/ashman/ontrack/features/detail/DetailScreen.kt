package de.ashman.ontrack.features.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.detail.ui.ErrorContent
import de.ashman.ontrack.features.detail.ui.LoadingContent
import de.ashman.ontrack.features.detail.ui.SuccessContent
import de.ashman.ontrack.navigation.LocalSnackbarHostState
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.track_status_removed
import ontrack.composeapp.generated.resources.track_status_removed_undo
import ontrack.composeapp.generated.resources.track_status_saved
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    media: Media,
    viewModel: DetailViewModel,
    onClickItem: (Media) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(media.id) {
        viewModel.fetchDetails(media)
    }

    when (uiState.resultState) {
        DetailResultState.Loading -> {
            LoadingContent()
        }

        DetailResultState.Error -> {
            ErrorContent()
        }

        DetailResultState.Success -> {
            SuccessContent(
                media = uiState.selectedMedia,
                onSaveTrack = {
                    viewModel.saveTrack(it)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(getString(Res.string.track_status_saved))
                    }
                },
                onRemoveTrack = {
                    viewModel.removeTrack()
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = getString(Res.string.track_status_removed),
                            actionLabel = getString(Res.string.track_status_removed_undo)
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.undoRemoveTrack()
                        }
                    }
                },
                onClickItem = onClickItem,
            )
        }
    }
}
