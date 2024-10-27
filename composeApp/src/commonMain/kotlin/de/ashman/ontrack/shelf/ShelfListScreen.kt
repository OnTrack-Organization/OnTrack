package de.ashman.ontrack.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.shelf.ui.AlbumViewModel
import de.ashman.ontrack.shelf.ui.BoardGameViewModel
import de.ashman.ontrack.shelf.ui.BookViewModel
import de.ashman.ontrack.media.domain.Media
import de.ashman.ontrack.media.domain.MediaType
import de.ashman.ontrack.media.domain.ConsumeStatus
import de.ashman.ontrack.shelf.ui.MovieViewModel
import de.ashman.ontrack.shelf.ui.ShowViewModel
import de.ashman.ontrack.shelf.ui.VideoGameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

// TODO screen hat eine column mit allen einträgen, pager, searchbar, fab und bottomsheet mit settings
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfListScreen(
    mediaType: MediaType,
    movieViewModel: MovieViewModel = koinInject(),
    showViewModel: ShowViewModel = koinInject(),
    bookViewModel: BookViewModel = koinInject(),
    videoGameViewModel: VideoGameViewModel = koinInject(),
    boardGameViewModel: BoardGameViewModel = koinInject(),
    albumViewModel: AlbumViewModel = koinInject(),
) {
    // TODO change so that we actually get saved items
    val shelfState = when (mediaType) {
        MediaType.MOVIE -> movieViewModel.uiState.collectAsState()
        MediaType.SHOW -> showViewModel.uiState.collectAsState()
        MediaType.BOOK -> bookViewModel.uiState.collectAsState()
        MediaType.VIDEOGAME -> videoGameViewModel.uiState.collectAsState()
        MediaType.BOARDGAME -> boardGameViewModel.uiState.collectAsState()
        MediaType.ALBUM -> albumViewModel.uiState.collectAsState()
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState())

    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredShelfItems = shelfState.value.mediaList
        .filter { it.name.contains(searchQuery, ignoreCase = true) }
        .ifEmpty { shelfState.value.mediaList }

    Scaffold(
        topBar = {
            FixedSearchBar(
                onSearchQueryChange = { query ->
                    searchQuery = query
                },
                searchQuery = searchQuery,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = "Add Icon")
            }
        }
    ) { innerPadding ->
        ShelfListContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            mediaType = mediaType,
            shelfItems = filteredShelfItems,
        )
    }

    if (showBottomSheet) {
        BottomSheet(
            onHide = {
                showBottomSheet = false
                CoroutineScope(Dispatchers.Main).launch {
                    sheetState.bottomSheetState.hide()
                }
            },
            sheetState = sheetState,
        )
    }
}

@Composable
fun ShelfListContent(
    modifier: Modifier,
    mediaType: MediaType,
    shelfItems: List<Media>,
) {
    val tabTitles = mediaType.consumeStatuses
    val pagerState = rememberPagerState { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index

                Tab(
                    selected = isSelected,
                    icon = {
                        Icon(
                            imageVector = title.getConsumeStatusIcon(isSelected),
                            contentDescription = title.name
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val statusType = tabTitles[page]
            val filteredMediaByStatus = if (statusType == ConsumeStatus.ALL) {
                shelfItems
            } else {
                shelfItems.filter { it.consumeStatus == statusType }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMediaByStatus) {
                    ShelfCard(it)
                }
            }
        }
    }
}

@Composable
fun ShelfCard(media: Media) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { }
    ) {
        // TODO pretty card
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = media.name, style = MaterialTheme.typography.titleMedium)
            Text(text = media.coverUrl, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixedSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        onSearch = { },
        active = false,
        onActiveChange = { },
        placeholder = { Text("Search") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear Search Icon")
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        tonalElevation = 0.dp
    ) {
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onHide: () -> Unit,
    sheetState: BottomSheetScaffoldState,
) {
    BottomSheetScaffold(
        sheetContent = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // TODO Settings, Filter etc.
                Text("Settings")
                Button(onClick = onHide) {
                    Text("Close")
                }
            }
        },
        scaffoldState = sheetState
    ) {}
}