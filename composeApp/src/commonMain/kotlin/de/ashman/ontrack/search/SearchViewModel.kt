package de.ashman.ontrack.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.boardgame.BoardGameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideoGameRepository
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videoGameRepository: VideoGameRepository,
    private val boardGameRepository: BoardGameRepository,
    private val albumRepository: AlbumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        Logger.d { "SearchViewModel init" }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        search()
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
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        val query = uiState.value.query

        viewModelScope.launch {
            val result = when (uiState.value.selectedMediaType) {
                MediaType.MOVIE -> movieRepository.fetchMediaByQuery(query)
                MediaType.SHOW -> showRepository.fetchMediaByQuery(query)
                MediaType.BOOK -> bookRepository.fetchMediaByQuery(query)
                MediaType.VIDEOGAME -> videoGameRepository.fetchMediaByQuery(query)
                MediaType.BOARDGAME -> boardGameRepository.fetchMediaByQuery(query)
                MediaType.ALBUM -> albumRepository.fetchMediaByQuery(query)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        searchResults = it,
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to fetch results: ${exception.message}"
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
