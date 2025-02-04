package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.util.getMediaTypeUi
import org.jetbrains.compose.resources.pluralStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    media: Media,
    viewModel: DetailViewModel,
    onClickItem: (Media) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = TopAppBarState(-Float.MAX_VALUE, 0F, 0F))
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(media.id) {
        viewModel.fetchDetails(media)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = media.mediaType.getMediaTypeUi().icon,
                            contentDescription = "Media Type Icon"
                        )
                        Text(text = pluralStringResource(media.mediaType.getMediaTypeUi().title, 1))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Icon")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            when (uiState.resultState) {
                DetailResultState.Loading -> {
                    LoadingContent()
                }

                DetailResultState.Error -> {
                    ErrorContent()
                }

                DetailResultState.Success -> {
                    uiState.selectedMedia?.let {
                        SuccessContent(
                            snackbarHostState = snackbarHostState,
                            media = it,
                            tracking = uiState.selectedTracking,
                            onSaveTracking = viewModel::saveTracking,
                            onDeleteTrackings = viewModel::deleteTrackings,
                            onClickItem = onClickItem,
                        )
                    }
                }
            }
        }
    }
}
