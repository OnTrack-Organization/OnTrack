package de.ashman.ontrack.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.db.MediaService
import de.ashman.ontrack.db.entity.toDomain
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.addTrackStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map
import kotlin.time.measureTime

class SearchViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
    private val mediaService: MediaService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState
        .onStart {
            observeSearchQuery()
        }
        .onEach {
            observeUserMedia()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var searchJob: Job? = null

    init {
        Logger.i { "SearchViewModel init" }
        searchJob = getTrending()
    }

    fun search(query: String) = viewModelScope.launch {
        val duration = measureTime {
            _uiState.update { it.copy(searchResultState = SearchResultState.Loading) }

            val repository = when (uiState.value.selectedMediaType) {
                MediaType.MOVIE -> movieRepository
                MediaType.SHOW -> showRepository
                MediaType.BOOK -> bookRepository
                MediaType.VIDEOGAME -> videogameRepository
                MediaType.BOARDGAME -> boardgameRepository
                MediaType.ALBUM -> albumRepository
            }

            repository.fetchByQuery(query).fold(
                onSuccess = { searchResults ->
                    _uiState.update {
                        it.copy(
                            searchResultState = if (searchResults.isEmpty()) SearchResultState.Empty else SearchResultState.Success,
                            errorMessage = null,
                            searchResults = searchResults,
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            searchResultState = SearchResultState.Error,
                            errorMessage = "Failed to fetch results: ${exception.message}",
                            searchResults = emptyList(),
                        )
                    }
                }
            )
        }

        _uiState.update { it.copy(searchDuration = duration.inWholeMilliseconds) }
        Logger.i { "${_uiState.value.selectedMediaType.name} Search took $duration" }
    }

    private fun getTrending() = viewModelScope.launch {
        val duration = measureTime {
            _uiState.update { it.copy(searchResultState = SearchResultState.Loading) }

            val repository = when (uiState.value.selectedMediaType) {
                MediaType.MOVIE -> movieRepository
                MediaType.SHOW -> showRepository
                MediaType.BOOK -> bookRepository
                MediaType.VIDEOGAME -> videogameRepository
                MediaType.BOARDGAME -> boardgameRepository
                MediaType.ALBUM -> albumRepository
            }

            repository.fetchTrending().fold(
                onSuccess = { trendingResults ->
                    _uiState.update {
                        it.copy(
                            searchResultState = if (trendingResults.isEmpty()) SearchResultState.Empty else SearchResultState.Success,
                            errorMessage = null,
                            cachedTrending = trendingResults,
                            searchResults = trendingResults,
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            searchResultState = SearchResultState.Error,
                            errorMessage = "Failed to fetch trending results: ${exception.message}",
                            cachedTrending = emptyList(),
                        )
                    }
                }
            )
        }

        _uiState.update { it.copy(searchDuration = duration.inWholeMilliseconds) }
        Logger.i { "${_uiState.value.selectedMediaType.name} Trending took $duration" }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        uiState
            .map { it.query }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        // Fetch trending if cached is empty
                        if (_uiState.value.cachedTrending.isEmpty()) {
                            searchJob?.cancel()
                            searchJob = getTrending()
                        } else {
                            // Set results to cached trending if query is empty
                            _uiState.update {
                                it.copy(
                                    errorMessage = null,
                                    searchResults = _uiState.value.cachedTrending,
                                )
                            }
                        }
                    }

                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = search(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeUserMedia() {
        mediaService.getUserMediaListFlow()
            .onEach { mediaEntityList ->
                _uiState.update {
                    // Update search results by user media from db
                    val enrichedSearchResults = it.searchResults.map { apiMedia ->
                        val mediaEntity = mediaEntityList.find { it.id == apiMedia.id }
                        if (mediaEntity != null) {
                            apiMedia.addTrackStatus(mediaEntity.trackStatus?.toDomain())
                        } else if (apiMedia.trackStatus != null) {
                            // If the media is no longer in the user's media list, reset its status
                            apiMedia.addTrackStatus(null)
                        } else {
                            // Keep media that hasn't been tracked
                            apiMedia
                        }
                    }

                    it.copy(searchResults = enrichedSearchResults)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onMediaTypeSelected(mediaType: MediaType) {
        _uiState.update { it.copy(selectedMediaType = mediaType, cachedTrending = emptyList()) }

        searchJob?.cancel()
        searchJob = if (_uiState.value.query.isBlank()) getTrending() else search(_uiState.value.query)
    }
}

data class SearchUiState(
    val searchResults: List<Media> = emptyList(),
    val cachedTrending: List<Media> = emptyList(),
    val query: String = "",
    val selectedMediaType: MediaType = MediaType.MOVIE,
    val searchResultState: SearchResultState = SearchResultState.Empty,
    val searchDuration: Long = 0L,
    val errorMessage: String? = null,
)

enum class SearchResultState {
    Empty,
    Loading,
    Success,
    Error,
}
