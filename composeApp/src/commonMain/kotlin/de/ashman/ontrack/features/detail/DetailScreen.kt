package de.ashman.ontrack.features.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.detail.ui.ErrorContent
import de.ashman.ontrack.features.detail.ui.LoadingContent
import de.ashman.ontrack.features.detail.ui.SuccessContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    media: Media,
    viewModel: DetailViewModel,
    onClickItem: (Media) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                onSaveTrackStatus = { viewModel.saveTrack(it) },
                onRemoveTrack = { viewModel.removeTrack() },
                onClickItem = onClickItem,
            )
        }
    }
}
