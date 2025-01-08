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

    fun fetchDetails(media: Media) {
        if (_uiState.value.selectedMedia?.id != media.id) {
            _uiState.value = _uiState.value.copy(selectedMedia = null)
        }

        _uiState.value = _uiState.value.copy(detailResultState = DetailResultState.Loading)

        viewModelScope.launch {
            val result = when (media.mediaType) {
                MediaType.MOVIE -> movieRepository.fetchMediaDetails(media.id)
                MediaType.SHOW -> showRepository.fetchMediaDetails(media.id)
                MediaType.BOOK -> {
                    // TODO fix
                    //bookRepository.fetchMediaDetails(id)
                    bookRepository.fetchMediaDetailsWithPartial(media as Book)
                }
                MediaType.VIDEOGAME -> videoGameRepository.fetchMediaDetails(media.id)
                MediaType.BOARDGAME -> boardGameRepository.fetchMediaDetails(media.id)
                MediaType.ALBUM -> albumRepository.fetchMediaDetails(media.id)
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
