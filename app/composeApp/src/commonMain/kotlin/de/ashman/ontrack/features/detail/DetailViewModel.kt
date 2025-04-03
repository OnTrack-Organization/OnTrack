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
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.SelectedMediaRepository
import de.ashman.ontrack.repository.firestore.RecommendationRepository
import de.ashman.ontrack.repository.firestore.TrackingRepository
import de.ashman.ontrack.util.getSingularTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_recommendation_added_to_catalog
import ontrack.composeapp.generated.resources.tracking_removed
import ontrack.composeapp.generated.resources.tracking_saved
import org.jetbrains.compose.resources.getString
import kotlin.time.measureTime

class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
    private val trackingRepository: TrackingRepository,
    private val recommendationRepository: RecommendationRepository,
    private val selectedMediaRepository: SelectedMediaRepository,
    private val currentUserRepository: CurrentUserRepository,
    private val commonUiManager: CommonUiManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _uiState.value,
        )

    init {
        viewModelScope.launch {
            selectedMediaRepository.selectedMedia.collect { media ->
                _uiState.update { it.copy(selectedMedia = media) }
            }
        }

        viewModelScope.launch {
            currentUserRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun observeTracking(mediaId: String) = viewModelScope.launch {
        trackingRepository.observeTrackings(currentUserRepository.currentUserId)
            .collect { trackings ->
                val tracking = trackings.find { it.mediaId == mediaId }
                _uiState.update { it.copy(selectedTracking = tracking) }
            }
    }

    fun observeFriendTrackings(mediaId: String) = viewModelScope.launch {
        trackingRepository.observeFriendTrackingsForMedia(mediaId).collect { friendTrackings ->
            _uiState.update { state ->
                state.copy(
                    friendTrackings = friendTrackings
                )
            }
        }
    }

    fun observeRatingStats(mediaId: String, mediaType: MediaType) = viewModelScope.launch {
        trackingRepository.observeRatingStats("${mediaType}_$mediaId").collectLatest { ratingStats ->
            ratingStats?.let {
                _uiState.update { it.copy(ratingStats = ratingStats) }
            }
        }
    }

    fun fetchDetails(mediaNavItems: MediaNavigationItems) = viewModelScope.launch {
        measureTime {
            _uiState.update {
                it.copy(
                    resultState = DetailResultState.Loading,
                    //selectedMedia = null,
                    ratingStats = RatingStats(),
                )
            }

            getRepository(mediaNavItems.mediaType).fetchDetails(mediaNavItems.id).fold(
                onSuccess = { media ->
                    _uiState.update {
                        it.copy(resultState = DetailResultState.Success)
                    }

                    selectedMediaRepository.selectMedia(media)
                },
                onFailure = { exception ->
                    _uiState.update { it.copy(resultState = DetailResultState.Error) }
                    Logger.e { "FAILED TO FETCH MEDIA DETAILS: ${exception.message}" }
                }
            )
        }.also { duration ->
            _uiState.update { it.copy(searchDuration = duration.inWholeMilliseconds) }
        }
    }

    fun saveTracking(tracking: Tracking) = viewModelScope.launch {
        commonUiManager.hideSheetAndShowSnackbar(
            getString(
                resource = Res.string.tracking_saved,
                tracking.mediaType.getSingularTitle(),
                getString(tracking.status?.getLabel(tracking.mediaType)!!)
            )
        )

        delay(500L)

        _uiState.update { it.copy(selectedTracking = tracking) }

        trackingRepository.saveTracking(tracking)
    }

    fun removeTracking(trackingId: String) = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch
        val tracking = _uiState.value.selectedTracking ?: return@launch

        commonUiManager.hideSheetAndShowSnackbar(
            getString(
                resource = Res.string.tracking_removed,
                tracking.mediaType.getSingularTitle(),
                getString(tracking.status?.getLabel(tracking.mediaType)!!)
            )
        )

        _uiState.update { it.copy(selectedTracking = null) }

        trackingRepository.removeTracking(trackingId = trackingId, ratingId = "${media.mediaType}_${media.id}")
    }

    fun addRecommendationToCatalog() = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch
        recommendationRepository.catalogRecommendation(media.id)

        _uiState.value.user?.let { user ->
            val catalogTracking = Tracking(
                userId = user.id,
                username = user.name,
                userImageUrl = user.imageUrl,
                mediaId = media.id,
                mediaType = media.mediaType,
                mediaTitle = media.title,
                mediaCoverUrl = media.coverUrl,
                status = TrackStatus.CATALOG,
                timestamp = System.now().toEpochMilliseconds(),
            )
            saveTracking(catalogTracking)
        }

        commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.detail_recommendation_added_to_catalog))
    }

    fun clearViewModel() {
        _uiState.update { DetailUiState() }
    }

    private fun getRepository(mediaType: MediaType) = when (mediaType) {
        MediaType.MOVIE -> movieRepository
        MediaType.SHOW -> showRepository
        MediaType.BOOK -> bookRepository
        MediaType.VIDEOGAME -> videogameRepository
        MediaType.BOARDGAME -> boardgameRepository
        MediaType.ALBUM -> albumRepository
    }
}

data class DetailUiState(
    val user: User? = null,
    val selectedMedia: Media? = null,
    val selectedTracking: Tracking? = null,
    val ratingStats: RatingStats = RatingStats(),
    val friendTrackings: List<Tracking> = emptyList(),
    val resultState: DetailResultState = DetailResultState.Loading,
    val isRefreshing: Boolean = false,
    val searchDuration: Long = 0L,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
