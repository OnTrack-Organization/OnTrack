package de.ashman.ontrack.shelf

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.model.ConsumeStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

// TODO screen hat eine column mit allen einträgen, pager, searchbar, fab und bottomsheet mit settings
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfListScreen(
    mediaType: MediaType,
    // TODO add ShelfViewModel
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState())
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val localFocusManager = LocalFocusManager.current

    /*val viewState = viewModel.uiState.collectAsState().value

    val filteredShelfItems = viewState.mediaList
        .filter { it.name.contains(searchQuery, ignoreCase = true) }
        .ifEmpty { viewState.mediaList }*/

    Scaffold(
        topBar = {
            FixedSearchBar(
                onSearchQueryChanged = { query ->
                    searchQuery = query
                },
                searchQuery = searchQuery,
            )
        }
    ) { innerPadding ->
        ShelfListContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                },
            mediaType = mediaType,
            shelfItems = emptyList(),
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
    val tabTitles = mediaType.consumeStatuses.toMutableList()
    tabTitles.add(0, ConsumeStatus.ALL)

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
                            modifier = Modifier.scale(1.5f).padding(16.dp),
                            imageVector = title.getConsumeStatusIcon(isSelected),
                            contentDescription = title.name
                        )
                    },
                    text = { Text(stringResource(title.getConsumeStatusLabel())) },
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
    onSearchQueryChanged: (String) -> Unit
) {
    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChanged,
                onSearch = { },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text("Search") },
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
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        tonalElevation = 0.dp,
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