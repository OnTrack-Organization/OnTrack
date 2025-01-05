package de.ashman.ontrack.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
        SearchBarWithFilterChips(
            query = viewState.query,
            onQueryChanged = viewModel::onQueryChanged,
            onSearch = viewModel::search,
            mediaTypes = MediaType.entries.toList(),
            selectedMediaType = viewState.selectedMediaType,
            onMediaTypeSelected = viewModel::onMediaTypeSelected,
            closeKeyboard = { localFocusManager.clearFocus() }
        )

        if (viewState.isLoading) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithFilterChips(
    query: String,
    mediaTypes: List<MediaType>,
    selectedMediaType: MediaType,
    onSearch: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onMediaTypeSelected: (MediaType) -> Unit,
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

    FilterChips(
        mediaTypes = mediaTypes,
        selectedMediaType = selectedMediaType,
        onMediaTypeSelected = onMediaTypeSelected,
    )
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