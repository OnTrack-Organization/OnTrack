package de.ashman.ontrack.shelf

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.media.model.StatusType
import de.ashman.ontrack.media.model.Movie
import de.ashman.ontrack.media.movie.ui.MovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/*val shelfItems = listOf(
    Movie(id = 1, title = "Attack on Titan", watchStatus = StatusType.WATCHED),
    Movie(id = 2, title = "Demon Slayer", watchStatus = StatusType.CATALOG),
    Movie(id = 3, title = "Breaking Bad", watchStatus = StatusType.CATALOG),
    Movie(id = 4, title = "Game of Thrones", watchStatus = StatusType.WATCHED),
    Movie(id = 5, title = "Naruto", watchStatus = StatusType.DROPPED),
)*/
val shelfItems = emptyList<Movie>()

// TODO screen hat eine column mit allen einträgen, pager, searchbar, fab und bottomsheet mit settings
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfListScreen(
    mediaType: MediaType,
    movieViewModel: MovieViewModel = koinInject(),
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState())

    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Filter shelf items based on the search query
    // TODO change to domain model
    val filteredShelfItems = shelfItems
        .filter { it.name?.contains(searchQuery, ignoreCase = true) ?: false }
        .ifEmpty { shelfItems }

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShelfListContent(
    modifier: Modifier,
    mediaType: MediaType,
    shelfItems: List<Movie>,
) {
    val tabTitles = mediaType.statusTypes
    val pagerState = rememberPagerState { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    icon = { Icon(imageVector = mediaType.getStatusIcon(title), contentDescription = title.name) },
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
            val filteredMoviesByStatus = if (statusType == StatusType.ALL) {
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
                items(filteredMoviesByStatus) { movie ->
                    ShelfCard(movie = movie)
                }
            }
        }
    }
}

@Composable
fun ShelfCard(movie: Movie) {
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
            if (movie.name != null) Text(text = movie.name, style = MaterialTheme.typography.titleMedium)
            movie.overview?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
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