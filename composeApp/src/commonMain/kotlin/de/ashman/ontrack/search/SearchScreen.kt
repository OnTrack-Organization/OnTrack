package de.ashman.ontrack.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SearchBarWithDropdown(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
            options = MediaType.entries.toList(),
            onOptionSelected = { selectedOption = it },
            selectedOption = selectedOption,
            onSearch = { viewModel.fetchMediaByQuery(searchQuery) },
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
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClickItem(item.id) }
    ) {
        AsyncImage(
            modifier = Modifier.size(300.dp),
            model = item.coverUrl,
            contentDescription = "Cover"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithDropdown(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    options: List<MediaType>,
    onOptionSelected: (MediaType) -> Unit,
    selectedOption: MediaType,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
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
                        } else {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    },
                )
            },
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(16.dp),
        ) {
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.title)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}