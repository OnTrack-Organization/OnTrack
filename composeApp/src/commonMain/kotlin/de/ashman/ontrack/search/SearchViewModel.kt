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
        Logger.d { "SearchViewModel init" }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun onMediaTypeSelected(mediaType: MediaType) {
        _uiState.value = _uiState.value.copy(
            selectedMediaType = mediaType,
            searchResults = emptyList()
        )
        search()
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
                MediaType.MOVIE -> movieRepository.fetchMediaByQuery(query)
                MediaType.SHOW -> showRepository.fetchMediaByQuery(query)
                MediaType.BOOK -> bookRepository.fetchMediaByQuery(query)
                MediaType.VIDEOGAME -> videogameRepository.fetchMediaByQuery(query)
                MediaType.BOARDGAME -> boardgameRepository.fetchMediaByQuery(query)
                MediaType.ALBUM -> albumRepository.fetchMediaByQuery(query)
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
}

data class SearchUiState(
    val searchResults: List<Media> = emptyList(),
    val query: String = "Naruto",
    val selectedMediaType: MediaType = MediaType.BOOK,
    val searchResultState: SearchResultState = SearchResultState.Empty,
    val errorMessage: String? = null,
)

enum class SearchResultState {
    Empty,
    Loading,
    Success,
    Error,
}
