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
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.TrackingService
import de.ashman.ontrack.db.entity.toDomain
import de.ashman.ontrack.db.entity.toEntity
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.navigation.MediaNavigationItems
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
    private val trackingService: TrackingService,
    private val authService: AuthService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun fetchDetails(mediaNavItems: MediaNavigationItems) = viewModelScope.launch {
        measureTime {
            _uiState.update { it.copy(resultState = DetailResultState.Loading) }

            mediaNavItems.mediaType.getRepository().fetchDetails(mediaNavItems.id).fold(
                onSuccess = { result ->
                    _uiState.update {
                        it.copy(
                            resultState = DetailResultState.Success,
                            selectedMedia = result,
                        )
                    }
                },
                onFailure = { exception ->
                    val errorMessage = "Failed to fetch details: ${exception.message}"
                    _uiState.update {
                        it.copy(
                            resultState = DetailResultState.Error,
                            errorMessage = errorMessage
                        )
                    }
                    Logger.e { errorMessage }
                }
            )
        }.also { duration ->
            _uiState.update { it.copy(searchDuration = duration.inWholeMilliseconds) }
        }
    }

    fun saveTracking(tracking: Tracking) = viewModelScope.launch {
        val trackingEntity = tracking.toEntity()
        trackingService.saveTracking(trackingEntity)

        _uiState.update { it.copy(selectedTracking = trackingEntity.toDomain()) }
    }

    fun removeTracking(trackingId: String) = viewModelScope.launch {
        trackingService.removeTracking(trackingId)
        _uiState.update { it.copy(selectedTracking = null) }
    }

    fun observeTracking(mediaId: String) = viewModelScope.launch {
        trackingService.fetchTrackings(authService.currentUserId)
            .collect { trackings ->
                val tracking = trackings.find { it.mediaId == mediaId }
                _uiState.update { it.copy(selectedTracking = tracking?.toDomain()) }
            }
    }

    fun observeFriendTrackings(mediaId: String) = viewModelScope.launch {
        trackingService.fetchFriendTrackings(mediaId).collect { feedTrackings ->
            _uiState.update { state ->
                state.copy(
                    friendTrackings = feedTrackings.map { it.toDomain() }
                )
            }
        }
    }

    fun clearViewModel() {
        _uiState.update { DetailUiState() }
    }

    private fun MediaType.getRepository() = when (this) {
        MediaType.MOVIE -> movieRepository
        MediaType.SHOW -> showRepository
        MediaType.BOOK -> bookRepository
        MediaType.VIDEOGAME -> videogameRepository
        MediaType.BOARDGAME -> boardgameRepository
        MediaType.ALBUM -> albumRepository
    }
}

data class DetailUiState(
    val selectedMedia: Media? = null,
    val selectedTracking: Tracking? = null,
    val friendTrackings: List<Tracking> = emptyList(),
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
