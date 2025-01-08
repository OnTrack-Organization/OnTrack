package de.ashman.ontrack.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.boardgame.BoardGameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideoGameRepository
import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.search.SearchResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videoGameRepository: VideoGameRepository,
    private val boardGameRepository: BoardGameRepository,
    private val albumRepository: AlbumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        Logger.d { "DetailViewModel init" }
    }

    fun fetchDetails(id: String, mediaType: MediaType, partialMedia: Media? = null) {
        if (_uiState.value.selectedMedia?.id != id) {
            _uiState.value = _uiState.value.copy(selectedMedia = null)
        }

        _uiState.value = _uiState.value.copy(detailResultState = DetailResultState.Loading)

        viewModelScope.launch {
            val result = when (mediaType) {
                MediaType.MOVIE -> movieRepository.fetchMediaDetails(id)
                MediaType.SHOW -> showRepository.fetchMediaDetails(id)
                MediaType.BOOK -> {
                    // TODO fix
                    //bookRepository.fetchMediaDetails(id)
                    bookRepository.fetchMediaDetailsWithPartial(id, partialMedia as? Book)
                }
                MediaType.VIDEOGAME -> videoGameRepository.fetchMediaDetails(id)
                MediaType.BOARDGAME -> boardGameRepository.fetchMediaDetails(id)
                MediaType.ALBUM -> albumRepository.fetchMediaDetails(id)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        detailResultState = DetailResultState.Success,
                        selectedMedia = it,
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        detailResultState = DetailResultState.Error,
                        errorMessage = "Failed to fetch details: ${exception.message}"
                    )
                }
            )
        }
    }
}

data class DetailUiState(
    val selectedMedia: Media? = null,
    val detailResultState: DetailResultState = DetailResultState.Loading,
    val errorMessage: String? = null,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
