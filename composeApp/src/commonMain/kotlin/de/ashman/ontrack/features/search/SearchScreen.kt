package de.ashman.ontrack.features.search

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.EmptyContent
import de.ashman.ontrack.features.common.ErrorContent
import de.ashman.ontrack.features.common.LoadingContent
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.SearchBar
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import org.jetbrains.compose.resources.pluralStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SearchBar(
                query = uiState.query,
                onQueryChanged = viewModel::onQueryChanged,
                placeholder = uiState.selectedMediaType.name.lowercase(),
                closeKeyboard = { localFocusManager.clearFocus() }
            )

            FilterChips(
                selectedMediaType = uiState.selectedMediaType,
                onMediaTypeSelected = viewModel::onMediaTypeSelected,
                listState = uiState.chipRowState,
            )
        }

        when (uiState.resultStates[uiState.selectedMediaType]) {
            SearchResultState.Empty -> EmptyContent(
                mediaType = uiState.selectedMediaType,
            )

            SearchResultState.Loading -> LoadingContent()

            SearchResultState.Error -> ErrorContent(
                text = uiState.selectedMediaType.getMediaTypeUi().error,
            )

            SearchResultState.Success -> {
                SuccessContent(
                    uiState = uiState,
                    onClickItem = onClickItem,
                )
            }

            else -> {}
        }
    }
}

@Composable
fun SuccessContent(
    uiState: SearchUiState,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = uiState.posterRowState,
    ) {
        items(items = uiState.searchResults, key = { it.id }) { media ->
            val tracking = uiState.trackings.associateBy { it.mediaId }

            MediaPoster(
                modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                title = media.title,
                coverUrl = media.coverUrl,
                trackStatusIcon = tracking[media.id]?.status?.getIcon(true),
                onClick = {
                    onClickItem(
                        MediaNavigationItems(
                            id = media.id,
                            title = media.title,
                            coverUrl = media.coverUrl,
                            mediaType = media.mediaType
                        )
                    )
                },
            )
        }
    }
}

@Composable
fun FilterChips(
    selectedMediaType: MediaType,
    onMediaTypeSelected: (MediaType) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = listState,
    ) {
        items(MediaType.entries) { mediaType ->
            val isSelected = selectedMediaType == mediaType

            FilterChip(
                selected = isSelected,
                onClick = { if (!isSelected) onMediaTypeSelected(mediaType) },
                label = { Text(pluralStringResource(mediaType.getMediaTypeUi().title, 2)) },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                        imageVector = if (isSelected) mediaType.getMediaTypeUi().icon else mediaType.getMediaTypeUi().outlinedIcon,
                        contentDescription = "Chip icon",
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
