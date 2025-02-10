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
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.measureTime

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

    fun fetchDetails(tracking: Tracking) = viewModelScope.launch {
        //_uiState.update { it.copy(selectedTracking = tracking) }
        observeLatestTracking(tracking.mediaId)

        val duration = measureTime {
            _uiState.update { it.copy(resultState = DetailResultState.Loading) }

            val repository = when (tracking.mediaType) {
                MediaType.MOVIE -> movieRepository
                MediaType.SHOW -> showRepository
                MediaType.BOOK -> bookRepository
                MediaType.VIDEOGAME -> videogameRepository
                MediaType.BOARDGAME -> boardgameRepository
                MediaType.ALBUM -> albumRepository
            }

            repository.fetchDetails(tracking.mediaId).fold(
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

        _uiState.update { it.copy(searchDuration = duration.inWholeMilliseconds) }
    }

    fun saveTracking(tracking: Tracking) = viewModelScope.launch {
        val trackingEntity = tracking.toEntity()
        firestoreService.saveTracking(trackingEntity)

        _uiState.update { it.copy(selectedTracking = trackingEntity.toDomain()) }
    }

    fun deleteTrackings() = viewModelScope.launch {
        val selectedTracking = _uiState.value.selectedTracking

        selectedTracking?.let {
            firestoreService.deleteTrackingsByMediaId(it.mediaId)
            _uiState.update { it.copy(selectedTracking = null) }
        }
    }

    fun observeLatestTracking(mediaId: String) = viewModelScope.launch {
        firestoreService.fetchTracking(mediaId)
            .collect { trackingEntity ->
                _uiState.update { it.copy(selectedTracking = trackingEntity?.toDomain()) }
            }
    }
}

data class DetailUiState(
    val selectedMedia: Media? = null,
    val selectedTracking: Tracking? = null,
    val resultState: DetailResultState = DetailResultState.Loading,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchDuration: Long = 0L,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
