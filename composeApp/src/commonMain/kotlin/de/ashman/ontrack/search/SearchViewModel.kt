package de.ashman.ontrack.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        Logger.i { "SearchViewModel init" }
        getTrending()
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun onMediaTypeSelected(mediaType: MediaType) {
        if (_uiState.value.selectedMediaType == mediaType) return

        _uiState.value = _uiState.value.copy(
            selectedMediaType = mediaType,
            searchResults = emptyList()
        )

        if (uiState.value.query.isEmpty()) getTrending() else search()
    }

    fun search() {
        if (uiState.value.query.isEmpty()) {
            _uiState.value = _uiState.value.copy(searchResultState = SearchResultState.Empty)
            return
        }

        _uiState.value = _uiState.value.copy(searchResultState = SearchResultState.Loading)

        val query = uiState.value.query

        viewModelScope.launch {
            val result = when (uiState.value.selectedMediaType) {
                MediaType.MOVIE -> movieRepository.fetchByQuery(query)
                MediaType.SHOW -> showRepository.fetchByQuery(query)
                MediaType.BOOK -> bookRepository.fetchByQuery(query)
                MediaType.VIDEOGAME -> videogameRepository.fetchByQuery(query)
                MediaType.BOARDGAME -> boardgameRepository.fetchByQuery(query)
                MediaType.ALBUM -> albumRepository.fetchByQuery(query)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        searchResultState = if (it.isEmpty()) SearchResultState.Empty else SearchResultState.Success,
                        searchResults = it,
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        searchResultState = SearchResultState.Error,
                        errorMessage = "Failed to fetch results: ${exception.message}"
                    )
                }
            )
        }
    }

    fun getTrending() {
        _uiState.value = _uiState.value.copy(searchResultState = SearchResultState.Loading)
        viewModelScope.launch {

            val result = when (uiState.value.selectedMediaType) {
                MediaType.MOVIE -> movieRepository.fetchTrending()
                MediaType.SHOW -> showRepository.fetchTrending()
                MediaType.BOOK -> bookRepository.fetchTrending()
                MediaType.VIDEOGAME -> videogameRepository.fetchTrending()
                MediaType.BOARDGAME -> boardgameRepository.fetchTrending()
                MediaType.ALBUM -> albumRepository.fetchTrending()
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        searchResultState = SearchResultState.Success,
                        searchResults = it,
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        searchResultState = SearchResultState.Error,
                        errorMessage = "Failed to fetch trending results: ${exception.message}"
                    )
                }
            )
        }
    }
}

data class SearchUiState(
    val searchResults: List<Media> = emptyList(),
    val query: String = "",
    val selectedMediaType: MediaType = MediaType.MOVIE,
    val searchResultState: SearchResultState = SearchResultState.Empty,
    val errorMessage: String? = null,
)

enum class SearchResultState {
    Empty,
    Loading,
    Success,
    Error,
}
