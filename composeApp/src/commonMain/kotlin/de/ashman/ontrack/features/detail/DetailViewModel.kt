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
import de.ashman.ontrack.db.AuthRepository
import de.ashman.ontrack.db.FriendRepository
import de.ashman.ontrack.db.RecommendationRepository
import de.ashman.ontrack.db.TrackingRepository
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.recommendation.RecommendationStatus
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.navigation.MediaNavigationItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import kotlin.time.measureTime

class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
    private val trackingRepository: TrackingRepository,
    private val authRepository: AuthRepository,
    private val recommendationRepository: RecommendationRepository,
    private val friendRepository: FriendRepository,
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
        trackingRepository.saveTracking(tracking)

        _uiState.update { it.copy(selectedTracking = tracking) }
    }

    fun removeTracking(trackingId: String) = viewModelScope.launch {
        trackingRepository.removeTracking(trackingId)
        _uiState.update { it.copy(selectedTracking = null) }
    }

    fun observeTracking(mediaId: String) = viewModelScope.launch {
        trackingRepository.fetchTrackings(authRepository.currentUserId)
            .collect { trackings ->
                val tracking = trackings.find { it.mediaId == mediaId }
                _uiState.update { it.copy(selectedTracking = tracking) }
            }
    }

    fun observeFriendTrackings(mediaId: String) = viewModelScope.launch {
        trackingRepository.fetchFriendTrackingsForMedia(mediaId).collect { friendTrackings ->
            _uiState.update { state ->
                state.copy(
                    friendTrackings = friendTrackings
                )
            }
        }
    }

    fun observeFriendRecommendations(mediaId: String) = viewModelScope.launch {
        recommendationRepository.fetchRecommendations(mediaId).collect { recommendations ->
            _uiState.update { state ->
                state.copy(recommendations = recommendations)
            }
        }
    }

    fun addToCatalog() = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch
        recommendationRepository.catalogRecommendation(media.id)

        val catalogTracking = Tracking(
            mediaId = media.id,
            mediaType = media.mediaType,
            mediaTitle = media.title,
            mediaCoverUrl = media.coverUrl,
            status = TrackStatus.CATALOG,
            timestamp = System.now().toEpochMilliseconds(),
        )

        saveTracking(catalogTracking)
    }

    fun passRecommendation() = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch
        recommendationRepository.passRecommendation(media.id)
    }

    fun sendRecommendation(friendId: String, message: String?) = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch

        val recommendation = Recommendation(
            userId = authRepository.currentUserId,
            username = authRepository.currentUserName,
            userImageUrl = authRepository.currentUserImage,
            mediaId = media.id,
            mediaType = media.mediaType,
            mediaTitle = media.title,
            mediaCoverUrl = media.coverUrl,
            message = message,
            status = RecommendationStatus.PENDING
        )

        recommendationRepository.sendRecommendation(friendId, recommendation)
    }


    fun fetchFriends() = viewModelScope.launch {
        friendRepository.getFriends().collect { friends ->
            _uiState.update { it.copy(friends = friends) }
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
    val friends: List<Friend> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
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
