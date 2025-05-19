package de.ashman.ontrack.features.shelflist

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelf_list_all
import ontrack.composeapp.generated.resources.shelves_filled
import ontrack.composeapp.generated.resources.shelves_outlined
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfListScreen(
    viewModel: ShelfListViewModel,
    userId: String,
    mediaType: MediaType,
    onClickItem: (MediaNavigationParam) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaType) {
        viewModel.updateSelectedMediaType(mediaType)
        viewModel.observeTrackings()
    }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = pluralStringResource(mediaType.getMediaTypeUi().title, 2),
                titleIcon = mediaType.getMediaTypeUi().outlinedIcon,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                actionIcon = Icons.Default.Tune,
                onClickAction = { },
                onClickNavigation = onBack,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            TrackStatusFilterChips(
                mediaType = mediaType,
                selectedTrackStatus = uiState.selectedStatus,
                onTrackStatusSelected = viewModel::updateSelectedStatus,
            )

            AnimatedContent(
                targetState = uiState.filteredTrackings.isEmpty() to uiState.selectedStatus,
                label = "EmptyContentAnimation"
            ) { (isEmpty, selectedStatus) ->
                if (isEmpty) {
                    selectedStatus?.let {
                        EmptyShelfList(
                            text = it.getLabel(mediaType),
                            icon = it.getIcon(),
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 92.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.filteredTrackings, key = { it.media.id }) {
                            MediaPoster(
                                coverUrl = it.media.coverUrl,
                                onClick = {
                                    onClickItem(
                                        MediaNavigationParam(
                                            id = it.media.id,
                                            title = it.media.title,
                                            coverUrl = it.media.coverUrl,
                                            type = it.media.type,
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
}

@Composable
fun TrackStatusFilterChips(
    mediaType: MediaType,
    selectedTrackStatus: TrackStatus?,
    onTrackStatusSelected: (TrackStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
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
                    labelColor = contentColorFor(TrackStatus.CATALOG.getColor()),
                    iconColor = contentColorFor(TrackStatus.CATALOG.getColor()),

                    selectedContainerColor = TrackStatus.CATALOG.getColor(),
                    selectedLabelColor = contentColorFor(TrackStatus.CATALOG.getColor()),
                    selectedLeadingIconColor = contentColorFor(TrackStatus.CATALOG.getColor()),
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
                    labelColor = contentColorFor(it.getColor()),
                    iconColor = contentColorFor(it.getColor()),

                    selectedContainerColor = it.getColor(),
                    selectedLabelColor = contentColorFor(it.getColor()),
                    selectedLeadingIconColor = contentColorFor(it.getColor()),
                )
            )
        }
    }
}

@Composable
fun EmptyShelfList(
    modifier: Modifier = Modifier,
    text: StringResource,
    icon: ImageVector,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}