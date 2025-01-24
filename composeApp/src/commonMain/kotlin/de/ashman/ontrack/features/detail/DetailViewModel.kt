package de.ashman.ontrack.features.detail

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
import de.ashman.ontrack.db.entity.toEntity
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.domain.addTrackStatus
import de.ashman.ontrack.domain.removeTrackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
    private val mediaService: MediaService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState
        .onStart {}
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    init {
        Logger.d { "DetailViewModel init" }
    }

    fun resetSelectedMedia(id: String) {
        if (_uiState.value.selectedMedia?.id != id) {
            _uiState.update { it.copy(selectedMedia = null) }
        }
    }

    fun fetchDetails(media: Media) = viewModelScope.launch {
        resetSelectedMedia(media.id)

        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        val dbResult = mediaService.getUserMediaById(media.id)
        val apiResult = when (media) {
            is Movie -> movieRepository.fetchDetails(media.id)
            is Show -> showRepository.fetchDetails(media.id)
            is Book -> bookRepository.fetchBookDescription(media)
            is Videogame -> videogameRepository.fetchDetails(media.id)
            is Boardgame -> boardgameRepository.fetchDetails(media.id)
            is Album -> albumRepository.fetchDetails(media.id)
        }

        apiResult.fold(
            onSuccess = { result ->
                val enrichedResult = result.addTrackStatus(dbResult?.trackStatus?.toDomain())

                _uiState.update {
                    it.copy(
                        resultState = DetailResultState.Success,
                        selectedMedia = enrichedResult
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        resultState = DetailResultState.Error,
                        errorMessage = "Failed to fetch details: ${exception.message}"
                    )
                }
                Logger.e { _uiState.value.errorMessage.toString() }
            }
        )
    }

    fun saveTrack(status: TrackStatus?) = viewModelScope.launch {
        val selectedMedia = _uiState.value.selectedMedia

        selectedMedia?.let {
            val mediaEntity = selectedMedia.toEntity().copy(trackStatus = status?.toEntity())

            mediaService.saveMedia(mediaEntity)

            _uiState.update { it.copy(selectedMedia = selectedMedia.addTrackStatus(status)) }
        }
    }

    fun removeTrack() = viewModelScope.launch {
        val selectedMedia = _uiState.value.selectedMedia

        selectedMedia?.let {
            mediaService.removeMedia(selectedMedia.id)

            _uiState.update { it.copy(selectedMedia = selectedMedia.removeTrackStatus()) }
        }
    }
}

data class DetailUiState(
    val selectedMedia: Media? = null,
    val resultState: DetailResultState = DetailResultState.Loading,
    val errorMessage: String? = null,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
