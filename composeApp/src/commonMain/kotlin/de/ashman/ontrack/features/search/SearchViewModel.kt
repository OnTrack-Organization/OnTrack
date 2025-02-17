package de.ashman.ontrack.features.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.TrackingService
import de.ashman.ontrack.db.entity.toDomain
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
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

class SearchViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
    private val trackingService: TrackingService,
    private val authService: AuthService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState
        .onStart {
            observeSearchQuery()
        }
        .onEach {
            observeUserTrackings()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    var chipRowState: LazyListState by mutableStateOf(LazyListState(0, 0))
    private var searchJob: Job? = null

    init {
        fetchAllTrending()
    }

    private fun fetchAllTrending() {
        _uiState.update { it.copy(resultStates = it.resultStates + (MediaType.entries.associateWith { SearchResultState.Loading })) }

        viewModelScope.launch {
            val trendingResults = mutableListOf<Media>()

            MediaType.entries.forEach { mediaType ->
                launch {
                    val results = mediaType.getRepository().fetchTrending().getOrElse { emptyList() }
                    trendingResults.addAll(results)

                    _uiState.update {
                        it.copy(
                            cachedTrending = trendingResults,
                            searchResults = trendingResults.filter { media -> media.mediaType == it.selectedMediaType },
                            resultStates = it.resultStates + (mediaType to if (results.isEmpty()) SearchResultState.Empty else SearchResultState.Success)
                        )
                    }
                }
            }
        }
    }

    fun search(query: String) = viewModelScope.launch {
        val mediaType = uiState.value.selectedMediaType

        _uiState.update { it.copy(resultStates = it.resultStates + (mediaType to SearchResultState.Loading)) }

        mediaType.getRepository().fetchByQuery(query).fold(
            onSuccess = { searchResults ->
                _uiState.update {
                    it.copy(
                        searchResults = searchResults.distinctBy { media -> media.id }
                            .ifEmpty { it.cachedTrending.filter { media -> media.mediaType == mediaType } }, // Restore trending if empty
                        errorMessage = null,
                        resultStates = it.resultStates + (mediaType to if (searchResults.isEmpty()) SearchResultState.Empty else SearchResultState.Success)
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        resultStates = it.resultStates + (mediaType to SearchResultState.Error),
                        errorMessage = "Failed to fetch results: ${exception.message}",
                    )
                }
            }
        )
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        uiState
            .map { it.query }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                val mediaType = uiState.value.selectedMediaType

                when {
                    query.isBlank() -> {
                        searchJob?.cancel()
                        val trendingResults = uiState.value.cachedTrending.filter { media -> media.mediaType == mediaType }
                        _uiState.update {
                            it.copy(
                                searchResults = trendingResults,
                                resultStates = it.resultStates + (mediaType to if (trendingResults.isEmpty()) SearchResultState.Empty else SearchResultState.Success)
                            )
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

    private fun observeUserTrackings() {
        trackingService.fetchTrackings(authService.currentUserId)
            .onEach { trackings ->
                _uiState.update { it.copy(trackings = trackings.map { it.toDomain() }) }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onMediaTypeSelected(mediaType: MediaType) {
        _uiState.update { it.copy(selectedMediaType = mediaType) }

        if (uiState.value.query.isNotBlank()) {
            search(uiState.value.query)
        } else {
            _uiState.update {
                it.copy(
                    searchResults = it.cachedTrending.filter { media -> media.mediaType == mediaType }
                )
            }
        }
    }

    private fun MediaType.getRepository() = when (this) {
        MediaType.MOVIE -> movieRepository
        MediaType.SHOW -> showRepository
        MediaType.BOOK -> bookRepository
        MediaType.VIDEOGAME -> videogameRepository
        MediaType.BOARDGAME -> boardgameRepository
        MediaType.ALBUM -> albumRepository
    }
}

data class SearchUiState(
    val searchResults: List<Media> = emptyList(),
    val cachedTrending: List<Media> = emptyList(),
    val trackings: List<Tracking> = emptyList(),
    val query: String = "",
    val selectedMediaType: MediaType = MediaType.MOVIE,
    val resultStates: Map<MediaType, SearchResultState> = MediaType.entries.associateWith { SearchResultState.Empty },
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)

enum class SearchResultState {
    Empty,
    Loading,
    Success,
    Error,
}
