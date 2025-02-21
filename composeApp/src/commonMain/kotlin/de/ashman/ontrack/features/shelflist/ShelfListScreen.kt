package de.ashman.ontrack.features.shelflist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.detail.tracking.getIcon
import de.ashman.ontrack.features.detail.tracking.getLabel
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelf_list_all
import ontrack.composeapp.generated.resources.shelves_filled
import ontrack.composeapp.generated.resources.shelves_outlined
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfListScreen(
    viewModel: ShelfListViewModel,
    userId: String,
    mediaType: MediaType,
    onClickItem: (MediaNavigationItems) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaType) {
        viewModel.updateSelectedMediaType(mediaType)
        viewModel.observeUserTrackings(userId)
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
                            imageVector = mediaType.getMediaTypeUi().outlinedIcon,
                            contentDescription = "Media Type Icon"
                        )
                        Text(
                            text = pluralStringResource(mediaType.getMediaTypeUi().title, 2),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
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
                onTrackStatusSelected = viewModel::updateSelectedTrackType,
                listState = viewModel.listState,
            )

            AnimatedContent(
                targetState = uiState.filteredTrackings,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() // Smooth fade transition
                },
                label = "MediaListAnimation"
            ) { trackings ->
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 92.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(trackings, key = { it.mediaId }) {
                        MediaPoster(
                            coverUrl = it.mediaCoverUrl,
                            onClick = {
                                onClickItem(
                                    MediaNavigationItems(
                                        id = it.mediaId,
                                        title = it.mediaTitle,
                                        coverUrl = it.mediaCoverUrl,
                                        mediaType = it.mediaType
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackStatusFilterChips(
    mediaType: MediaType,
    selectedTrackStatus: TrackStatus?,
    onTrackStatusSelected: (TrackStatus?) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = listState,
    ) {
        item {
            val isSelected = selectedTrackStatus == null

            FilterChip(
                selected = isSelected,
                onClick = { if (!isSelected) onTrackStatusSelected(null) },
                label = { Text(stringResource(Res.string.shelf_list_all)) },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                        imageVector = if (isSelected) vectorResource(Res.drawable.shelves_filled) else vectorResource(Res.drawable.shelves_outlined),
                        contentDescription = "All Trackings",
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }

        items(TrackStatus.entries) {
            val isSelected = selectedTrackStatus == it

            FilterChip(
                selected = isSelected,
                onClick = { if (!isSelected) onTrackStatusSelected(it) },
                label = { Text(stringResource(it.getLabel(mediaType))) },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                        imageVector = it.getIcon(isSelected),
                        contentDescription = "Chip Icon",
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    }
}