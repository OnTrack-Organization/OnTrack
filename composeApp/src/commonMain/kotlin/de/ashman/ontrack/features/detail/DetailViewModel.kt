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
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.entity.MediaEntity
import de.ashman.ontrack.domain.sub.TrackStatus
import de.ashman.ontrack.domain.sub.TrackStatusEnum
import de.ashman.ontrack.domain.sub.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
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

    fun fetchDetails(media: Media) = viewModelScope.launch {
        if (_uiState.value.selectedMedia?.id != media.id) {
            _uiState.update { it.copy(selectedMedia = null) }
        }

        _uiState.update { it.copy(detailResultState = DetailResultState.Loading) }

        val result = when (media.mediaType) {
            MediaType.MOVIE -> movieRepository.fetchDetails(media.id)
            MediaType.SHOW -> showRepository.fetchDetails(media.id)
            MediaType.BOOK -> bookRepository.fetchBookDescription(media as Book)
            MediaType.VIDEOGAME -> videogameRepository.fetchDetails(media.id)
            MediaType.BOARDGAME -> boardgameRepository.fetchDetails(media.id)
            MediaType.ALBUM -> albumRepository.fetchDetails(media.id)
        }

        result.fold(
            onSuccess = { result ->
                _uiState.update {
                    it.copy(
                        detailResultState = DetailResultState.Success,
                        selectedMedia = result,
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        detailResultState = DetailResultState.Error,
                        errorMessage = "Failed to fetch details: ${exception.message}"
                    )
                }
                Logger.e { _uiState.value.errorMessage.toString() }
            }
        )
    }

    fun saveTrack(status: TrackStatusEnum, review: String) = viewModelScope.launch {
        val selectedMedia = _uiState.value.selectedMedia
        if (selectedMedia == null) return@launch

        val trackStatus = TrackStatus(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now().toEpochMilliseconds(),
            status = status,
            review = review,
            rating = null,
        )

        _uiState.update { it.copy(trackStatus = trackStatus) }

        val mediaEntity = MediaEntity(
            id = selectedMedia.id,
            name = selectedMedia.name,
            coverUrl = selectedMedia.coverUrl,
            type = selectedMedia.mediaType,
            trackStatus = trackStatus.toEntity(),
        )

        mediaService.saveMediaEntity(mediaEntity)

        Logger.d { "MediaEntity saved with updated TrackStatus: $mediaEntity" }
    }
}

data class DetailUiState(
    val selectedMedia: Media? = null,
    val trackStatus: TrackStatus? = null,
    val detailResultState: DetailResultState = DetailResultState.Loading,
    val errorMessage: String? = null,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
