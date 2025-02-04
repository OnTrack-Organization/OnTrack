package de.ashman.ontrack.features.shelflist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.track.getLabel
import de.ashman.ontrack.features.track.getStatusIcon
import de.ashman.ontrack.util.getMediaTypeUi
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfListScreen(
    viewModel: ShelfListViewModel,
    mediaType: MediaType,
    onClickItem: (Media) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaType) {
        viewModel.updateSelectedMediaType(mediaType)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = mediaType.getMediaTypeUi().icon,
                            contentDescription = "Media Type Icon"
                        )
                        Text(text = pluralStringResource(mediaType.getMediaTypeUi().title, 2))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Icon")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Tune, "Filter Icon")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            TrackStatusFilterChips(
                mediaType = mediaType,
                selectedTrackStatus = uiState.selectedStatus,
                onTrackTypeSelected = viewModel::updateSelectedTrackType,
                listState = viewModel.listState,
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 92.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(uiState.mediaList) {
                    MediaPoster(
                        coverUrl = it.coverUrl,
                        onClick = { onClickItem(it) },
                    )
                }
            }
        }
    }
}

@Composable
fun TrackStatusFilterChips(
    mediaType: MediaType,
    selectedTrackStatus: TrackStatus,
    onTrackTypeSelected: (TrackStatus) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = listState,
    ) {
        items(TrackStatus.entries) {
            FilterChip(
                selected = selectedTrackStatus == it,
                onClick = { if (selectedTrackStatus != it) onTrackTypeSelected(it) },
                label = { Text(stringResource(it.getLabel(mediaType))) },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                        imageVector = it.getStatusIcon(true),
                        contentDescription = "Chip Icon",
                    )
                }
            )
        }
    }
}