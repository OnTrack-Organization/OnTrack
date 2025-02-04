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
import de.ashman.ontrack.db.FirestoreService
import de.ashman.ontrack.db.toDomain
import de.ashman.ontrack.db.toEntity
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.Tracking
import de.ashman.ontrack.domain.Videogame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val firestoreService: FirestoreService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    init {
        Logger.d { "DetailViewModel init" }
    }

    fun fetchDetails(media: Media) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        observeLatestTracking(media.id)

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

    fun saveTracking(tracking: Tracking) = viewModelScope.launch {
        saveOrUpdateMedia()

        val trackingEntity = tracking.toEntity()
        firestoreService.saveTracking(trackingEntity)

        _uiState.update { it.copy(selectedTracking = trackingEntity.toDomain()) }
    }

    fun deleteTrackings() = viewModelScope.launch {
        val selectedTracking = _uiState.value.selectedTracking

        selectedTracking?.let {
            firestoreService.deleteTrackingsByMediaId(it.mediaId, selectedTracking.rating)
            _uiState.update { it.copy(selectedTracking = null) }
        }
    }

    fun observeLatestTracking(mediaId: String) = viewModelScope.launch {
        firestoreService.consumeLatestUserTracking(mediaId)
            .collect { trackingEntity ->
                _uiState.update { it.copy(selectedTracking = trackingEntity?.toDomain()) }
            }
    }

    private suspend fun saveOrUpdateMedia() {
        val selectedMedia = _uiState.value.selectedMedia
        selectedMedia?.let {
            firestoreService.saveMedia(it.toEntity())
        }
    }
}

data class DetailUiState(
    val selectedMedia: Media? = null,
    val selectedTracking: Tracking? = null,
    val resultState: DetailResultState = DetailResultState.Loading,
    val errorMessage: String? = null,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
