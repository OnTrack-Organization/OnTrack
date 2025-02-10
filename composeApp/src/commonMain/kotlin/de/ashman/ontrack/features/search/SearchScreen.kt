package de.ashman.ontrack.features.search

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.keyboardAsState
import de.ashman.ontrack.features.detail.components.EmptyContent
import de.ashman.ontrack.features.detail.components.ErrorContent
import de.ashman.ontrack.features.detail.components.LoadingContent
import de.ashman.ontrack.features.tracking.getStatusIcon
import de.ashman.ontrack.util.getMediaTypeUi
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onClickItem: (Tracking) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SearchBar(
                query = uiState.query,
                selectedMediaType = uiState.selectedMediaType,
                onQueryChanged = viewModel::onQueryChanged,
                closeKeyboard = { localFocusManager.clearFocus() }
            )

            FilterChips(
                selectedMediaType = uiState.selectedMediaType,
                onMediaTypeSelected = viewModel::onMediaTypeSelected,
                listState = viewModel.chipRowState,
            )
        }

        PullToRefreshBox(
            modifier = Modifier.fillMaxHeight(),
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.5f),
            ) {
                item {
                    when (uiState.searchResultState) {
                        SearchResultState.Empty -> EmptyContent(uiState.selectedMediaType)

                        SearchResultState.Loading -> LoadingContent()

                        SearchResultState.Error -> ErrorContent(
                            text = uiState.selectedMediaType.getMediaTypeUi().error
                        )

                        SearchResultState.Success ->
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                            ) {
                                items(items = uiState.searchResults, key = { it.id }) { media ->
                                    val tracking = uiState.trackings.find { it.mediaId == media.id }

                                    MediaPoster(
                                        modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                                        title = media.title,
                                        coverUrl = media.coverUrl,
                                        trackStatusIcon = tracking?.status?.getStatusIcon(true),
                                        onClick = {
                                            onClickItem(
                                                tracking ?: Tracking(
                                                    mediaId = media.id,
                                                    mediaType = media.mediaType,
                                                    mediaTitle = media.title,
                                                    mediaCoverUrl = media.coverUrl,
                                                    userId = Firebase.auth.currentUser?.uid,
                                                    userImageUrl = Firebase.auth.currentUser?.photoURL,
                                                    username = Firebase.auth.currentUser?.displayName,
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
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.End),
            text = "Search took ${uiState.searchDuration}ms"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    selectedMediaType: MediaType,
    closeKeyboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isKeyboardOpen by keyboardAsState()

    SearchBar(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { newQuery ->
                    onQueryChanged(newQuery)
                },
                onSearch = {
                    closeKeyboard()
                },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text(stringResource(selectedMediaType.getMediaTypeUi().searchPlaceholder)) },
                leadingIcon = {
                    if (isKeyboardOpen) {
                        IconButton(onClick = closeKeyboard) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back Arrow")
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search Icon")
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear Search Icon")
                        }
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = { },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(top = 0.dp),
    ) {}
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
            FilterChip(
                selected = selectedMediaType == mediaType,
                onClick = {
                    if (selectedMediaType != mediaType) onMediaTypeSelected(mediaType)
                },
                label = { Text(pluralStringResource(mediaType.getMediaTypeUi().title, 2)) },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                        imageVector = mediaType.getMediaTypeUi().icon,
                        contentDescription = "Chip icon",
                    )
                }
            )
        }
    }
}