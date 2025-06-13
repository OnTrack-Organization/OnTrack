package de.ashman.ontrack.features.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.ads.NativeAdPoster
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackTopBar
import de.ashman.ontrack.features.common.SearchBar
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.util.fakeItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.search_empty
import ontrack.composeapp.generated.resources.search_nav_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onClickItem: (MediaNavigationParam) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.observeTrackings()
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        },
        topBar = { OnTrackTopBar(title = stringResource(Res.string.search_nav_title)) },
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SearchBar(
                    query = uiState.query,
                    onQueryChanged = viewModel::onQueryChanged,
                    placeholder = pluralStringResource(uiState.selectedMediaType.getMediaTypeUi().title, 2).lowercase(),
                    closeKeyboard = { localFocusManager.clearFocus() }
                )

                FilterChips(
                    selectedMediaType = uiState.selectedMediaType,
                    onMediaTypeSelected = viewModel::onMediaTypeSelected,
                    listState = uiState.chipRowState,
                )
            }

            AnimatedContent(
                targetState = uiState.resultStates[uiState.selectedMediaType],
                label = "ResultStateAnimation"
            ) { state ->
                when (state) {
                    SearchResultState.Empty -> EmptyContent(
                        mediaType = uiState.selectedMediaType,
                    )

                    SearchResultState.Error -> ErrorContent(
                        text = uiState.selectedMediaType.getMediaTypeUi().error,
                    )

                    SearchResultState.Loading,
                    SearchResultState.Success -> {
                        SuccessContent(
                            uiState = uiState,
                            onClickItem = onClickItem,
                            isLoadingShimmer = state == SearchResultState.Loading
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun SuccessContent(
    uiState: SearchUiState,
    isLoadingShimmer: Boolean = false,
    onClickItem: (MediaNavigationParam) -> Unit = {},
) {
    val itemsToDisplay = if (isLoadingShimmer) {
        fakeItems().map { SearchItem.MediaItem(it) }
    } else {
        interleaveAds(uiState.searchResults)
    }

    val posterRowState = rememberLazyListState()
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(uiState.query, uiState.selectedMediaType) {
        posterRowState.scrollToItem(0)
    }

    LaunchedEffect(posterRowState) {
        snapshotFlow { posterRowState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    localFocusManager.clearFocus()
                }
            }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        state = posterRowState,
    ) {
        items(items = itemsToDisplay, key = {
            when (it) {
                is SearchItem.AdItem -> "ad-${it.index}"
                is SearchItem.MediaItem -> it.media.id
            }
        }) { item ->
            when (item) {
                is SearchItem.AdItem -> {
                    NativeAdPoster(
                        modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                    )
                }

                is SearchItem.MediaItem -> {
                    val media = item.media
                    val tracking = uiState.trackings.associateBy { it.media.id }

                    MediaPoster(
                        modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                        title = media.title,
                        coverUrl = media.coverUrl,
                        trackStatusIcon = tracking[media.id]?.status?.getIcon(true),
                        isLoadingShimmer = isLoadingShimmer,
                        onClick = {
                            onClickItem(
                                MediaNavigationParam(
                                    id = media.id,
                                    title = media.title,
                                    coverUrl = media.coverUrl,
                                    type = media.mediaType,
                                )
                            )
                        },
                    )
                }
            }
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

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.scale(1.5f))
    }
}

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    text: StringResource,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.Default.WifiOff,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "Error Icon"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyContent(
    mediaType: MediaType,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.Default.HideSource,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "No Results Icon"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(Res.string.search_empty, pluralStringResource(mediaType.getMediaTypeUi().title, 2).lowercase()),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}