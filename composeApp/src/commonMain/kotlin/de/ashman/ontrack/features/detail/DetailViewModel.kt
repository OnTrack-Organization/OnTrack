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
import de.ashman.ontrack.domain.RatingStats
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.domain.addTrackStatus
import de.ashman.ontrack.domain.removeTrackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        .onEach {
            observeSelectedMedia()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var lastRemovedMedia: Media? = null

    init {
        Logger.d { "DetailViewModel init" }
    }

    fun fetchDetails(media: Media) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        val repository = when (media) {
            is Movie -> movieRepository
            is Show -> showRepository
            is Book -> bookRepository
            is Videogame -> videogameRepository
            is Boardgame -> boardgameRepository
            is Album -> albumRepository
        }

        repository.fetchDetails(media).fold(
            onSuccess = { result ->
                _uiState.update {
                    it.copy(
                        resultState = DetailResultState.Success,
                        selectedMedia = result,
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
            val trackEntity = status?.toEntity()
            val mediaEntity = selectedMedia.toEntity().copy(trackStatus = trackEntity)

            mediaService.saveMedia(mediaEntity)

            _uiState.update { it.copy(selectedMedia = selectedMedia.addTrackStatus(trackEntity?.toDomain())) }
        }
    }

    fun removeTrack() = viewModelScope.launch {
        val selectedMedia = _uiState.value.selectedMedia

        selectedMedia?.let {
            lastRemovedMedia = it
            mediaService.removeMedia(selectedMedia.id)

            _uiState.update { it.copy(selectedMedia = selectedMedia.removeTrackStatus()) }
        }
    }

    fun undoRemoveTrack() = viewModelScope.launch {
        lastRemovedMedia?.let { media ->
            mediaService.saveMedia(media.toEntity())
            _uiState.update { it.copy(selectedMedia = media) }
        }
    }

    private fun observeSelectedMedia() {
        _uiState.value.selectedMedia?.let {
            mediaService.getUserMediaFlow(it.id)
                .onEach { mediaEntity ->
                    if (mediaEntity != null) {
                        val updatedMedia = it.addTrackStatus(mediaEntity.trackStatus?.toDomain())
                        _uiState.update { it.copy(selectedMedia = updatedMedia) }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun getMediaRatingStats(mediaId: String): StateFlow<RatingStats> {
        return mediaService.getMediaRatingStatsFlow(mediaId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, RatingStats(0, 0.0, 0, 0.0))
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
