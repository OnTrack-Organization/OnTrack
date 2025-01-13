package de.ashman.ontrack.search

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.detail.MediaPoster
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.util.keyboardAsState
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onClickItem: (Media) -> Unit = { },
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
        Column {
            SearchBar(
                query = uiState.query,
                selectedMediaType = uiState.selectedMediaType,
                onQueryChanged = viewModel::onQueryChanged,
                closeKeyboard = { localFocusManager.clearFocus() }
            )

            FilterChips(
                mediaTypes = MediaType.entries.toList(),
                selectedMediaType = uiState.selectedMediaType,
                onMediaTypeSelected = viewModel::onMediaTypeSelected,
            )
        }

        when (uiState.searchResultState) {
            SearchResultState.Empty -> EmptySearch(title = stringResource(uiState.selectedMediaType.title))
            SearchResultState.Loading -> LoadingSearch()
            SearchResultState.Error -> ErrorSearch()
            SearchResultState.Success ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(uiState.searchResults) {
                        MediaPoster(
                            name = it.name,
                            coverUrl = it.coverUrl,
                            onClickItem = { onClickItem(it) },
                        )
                    }
                }
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
fun ErrorSearch() {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Default.Error,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "Error Icon"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Network error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        contentPadding = PaddingValues(horizontal = 16.dp),
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