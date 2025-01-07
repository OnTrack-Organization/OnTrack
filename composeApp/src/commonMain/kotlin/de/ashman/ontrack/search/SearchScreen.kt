package de.ashman.ontrack.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.util.keyboardAsState
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onClickItem: (String, MediaType) -> Unit = { _, _ -> },
) {
    val localFocusManager = LocalFocusManager.current
    val viewState = viewModel.uiState.collectAsState().value

    Column(
        modifier = modifier
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SearchBar(
            query = viewState.query,
            selectedMediaType = viewState.selectedMediaType,
            onQueryChanged = viewModel::onQueryChanged,
            onSearch = viewModel::search,
            closeKeyboard = { localFocusManager.clearFocus() }
        )

        FilterChips(
            mediaTypes = MediaType.entries.toList(),
            selectedMediaType = viewState.selectedMediaType,
            onMediaTypeSelected = viewModel::onMediaTypeSelected,
        )

        if (viewState.isLoading) {
            LoadingSearch()
        } else if (viewState.searchResults.isEmpty()) {
            EmptySearch(title = stringResource(viewState.selectedMediaType.title))
        } else {
            SearchItemRow(
                viewState = viewState,
                onClickItem = onClickItem
            )
        }
    }
}

@Composable
fun LoadingSearch() {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.scale(1.5f))
    }
}

@Composable
fun EmptySearch(
    title: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Default.HideSource,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "No Results Icon"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "No $title found",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun SearchItem(
    item: Media,
    onClickItem: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = item.coverUrl,
        contentScale = ContentScale.Crop,
        contentDescription = "Cover",
        modifier = modifier.clickable {
            onClickItem(item.id)
        }
    ) {
        val state = painter.state.collectAsState().value

        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Card(modifier = Modifier.size(width = 200.dp, height = 300.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(modifier = Modifier.scale(1.5f))
                    }
                }
            }

            is AsyncImagePainter.State.Error -> {
                Card(modifier = Modifier.size(width = 200.dp, height = 300.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.name)
                    }
                }
            }

            else -> {
                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .size(width = 200.dp, height = 300.dp)
                        .shadow(elevation = 8.dp, RoundedCornerShape(12.dp))
                        .clip(shape = RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

@Composable
fun SearchItemRow(
    viewState: SearchUiState,
    onClickItem: (String, MediaType) -> Unit = { _, _ -> },
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(viewState.searchResults) {
            SearchItem(
                item = it,
                onClickItem = { id -> onClickItem(id, it.type) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    selectedMediaType: MediaType,
    onSearch: () -> Unit,
    onQueryChanged: (String) -> Unit,
    closeKeyboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isKeyboardOpen by keyboardAsState()

    SearchBar(
        modifier = modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChanged,
                onSearch = { onSearch() },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text("Search for ${stringResource(selectedMediaType.title)}") },
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
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(top = 0.dp),
    ) {}
}

@Composable
fun FilterChips(
    mediaTypes: List<MediaType>,
    selectedMediaType: MediaType,
    onMediaTypeSelected: (MediaType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(mediaTypes) { option ->
            FilterChip(
                selected = selectedMediaType == option,
                onClick = { onMediaTypeSelected(option) },
                label = { Text(stringResource(option.title)) },
                leadingIcon = {
                    Icon(
                        imageVector = option.icon(),
                        contentDescription = "Chip icon",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
        }
    }
}