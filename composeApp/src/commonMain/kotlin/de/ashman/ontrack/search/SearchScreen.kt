package de.ashman.ontrack.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.domain.Media
import de.ashman.ontrack.media.domain.MediaType
import de.ashman.ontrack.shelf.ui.AlbumViewModel
import de.ashman.ontrack.shelf.ui.BoardGameViewModel
import de.ashman.ontrack.shelf.ui.BookViewModel
import de.ashman.ontrack.shelf.ui.MovieViewModel
import de.ashman.ontrack.shelf.ui.ShowViewModel
import de.ashman.ontrack.shelf.ui.VideoGameViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onClickItem: (String, MediaType) -> Unit = { _, _ -> },
) {
    var selectedOption by rememberSaveable { mutableStateOf(MediaType.MOVIE) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val localFocusManager = LocalFocusManager.current

    val viewModel: MediaViewModel<*> = when (selectedOption) {
        MediaType.MOVIE -> koinInject<MovieViewModel>()
        MediaType.SHOW -> koinInject<ShowViewModel>()
        MediaType.BOOK -> koinInject<BookViewModel>()
        MediaType.VIDEOGAME -> koinInject<VideoGameViewModel>()
        MediaType.BOARDGAME -> koinInject<BoardGameViewModel>()
        MediaType.ALBUM -> koinInject<AlbumViewModel>()
    }

    val viewState = viewModel.uiState.collectAsState().value

    Column(
        modifier = modifier.padding(16.dp).pointerInput(Unit) {
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SearchBarWithFilterChips(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
            onSearch = { viewModel.fetchMediaByQuery(searchQuery) },
            options = MediaType.entries.toList(),
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it },
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(viewState.mediaList) {
                SearchItem(
                    item = it,
                    onClickItem = { id -> onClickItem(id, it.type) }
                )
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
                CircularProgressIndicator()
            }

            is AsyncImagePainter.State.Error -> {
                Card(modifier = Modifier.size(width = 200.dp, height = 300.dp)) {}
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
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    options: List<MediaType>,
    selectedOption: MediaType,
    onOptionSelected: (MediaType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    onSearch = { onSearch() },
                    expanded = false,
                    onExpandedChange = { },
                    placeholder = { Text("Search for ${stringResource(selectedOption.title)}") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear Search Icon")
                            }
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(options) { option ->
                FilterChip(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
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
}
