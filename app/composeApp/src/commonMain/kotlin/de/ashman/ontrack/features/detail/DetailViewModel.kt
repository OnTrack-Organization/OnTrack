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
import de.ashman.ontrack.domain.media.toDto
import de.ashman.ontrack.domain.newdomains.NewTracking
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.network.services.tracking.TrackingService
import de.ashman.ontrack.network.services.tracking.dto.CreateTrackingDto
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
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.tracking_create_error
import ontrack.composeapp.generated.resources.tracking_delete_error
import ontrack.composeapp.generated.resources.tracking_removed
import ontrack.composeapp.generated.resources.tracking_saved
import ontrack.composeapp.generated.resources.tracking_update_error
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
    private val commonUiManager: CommonUiManager,
    private val trackingService: TrackingService,
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
                _uiState.update { it.copy(currentMedia = media) }
            }
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

    fun selectStatus(status: TrackStatus) {
        _uiState.update { it.copy(currentStatus = status) }
    }

    fun saveTracking() {
        if (_uiState.value.currentTracking == null) {
            createTracking()
        } else {
            updateTracking()
        }
    }

    private fun createTracking() = viewModelScope.launch {
        val dto = CreateTrackingDto(
            media = _uiState.value.currentMedia!!.toDto(),
            status = _uiState.value.currentStatus!!,
        )

        trackingService.createTracking(dto).fold(
            onSuccess = { tracking ->
                commonUiManager.hideSheetAndShowSnackbar(
                    getString(
                        resource = Res.string.tracking_saved,
                        tracking.media.type.getSingularTitle(),
                        getString(tracking.status.getLabel(tracking.media.type))
                    )
                )

                delay(500L)
                _uiState.update { it.copy(currentTracking = tracking) }

                Logger.d { "New tracking created: $tracking" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(currentStatus = null) }
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_create_error))
                Logger.e { "Failed to create tracking: ${exception.message}" }
            }
        )
    }

    private fun updateTracking() = viewModelScope.launch {
        trackingService.updateTrackStatus(_uiState.value.currentStatus!!).fold(
            onSuccess = {
                commonUiManager.hideSheetAndShowSnackbar(
                    getString(
                        resource = Res.string.tracking_saved,
                        _uiState.value.currentMedia!!.mediaType.getSingularTitle(),
                        getString(_uiState.value.currentStatus!!.getLabel(_uiState.value.currentMedia!!.mediaType))
                    )
                )

                delay(500L)
                _uiState.update { it.copy(currentTracking = it.currentTracking?.copy(status = _uiState.value.currentStatus!!)) }

                Logger.d { "Tracking updated: ${_uiState.value.currentTracking}" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(currentStatus = null) }
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_update_error))
                Logger.e { "Failed to update tracking: ${exception.message}" }
            }
        )
    }

    fun removeTracking() = viewModelScope.launch {
        val tracking = _uiState.value.currentTracking ?: return@launch

        trackingService.deleteTracking(tracking.id).fold(
            onSuccess = {
                commonUiManager.hideSheetAndShowSnackbar(
                    getString(
                        resource = Res.string.tracking_removed,
                        tracking.media.type.getSingularTitle(),
                        getString(tracking.status.getLabel(tracking.media.type))
                    )
                )

                _uiState.update { it.copy(currentTracking = null) }
                Logger.d { "Tracking removed: $tracking" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(currentStatus = null) }
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_delete_error))
                Logger.e { "Failed to remove tracking: ${exception.message}" }
            }
        )
    }

    // TODO add back in later
    fun addRecommendationToCatalog() = viewModelScope.launch {
        /*val media = _uiState.value.selectedMedia ?: return@launch
        recommendationRepository.catalogRecommendation(media.id)

        _uiState.value.user?.let { user ->
            val catalogTracking = Tracking(
                userId = user.id,
                username = user.name,
                userImageUrl = user.profilePictureUrl,
                mediaId = media.id,
                mediaType = media.mediaType,
                mediaTitle = media.title,
                mediaCoverUrl = media.coverUrl,
                status = TrackStatus.CATALOG,
                timestamp = System.now().toEpochMilliseconds(),
            )
            saveTracking(catalogTracking)
        }

        commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.detail_recommendation_added_to_catalog))*/
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
    val currentMedia: Media? = null,
    val currentTracking: NewTracking? = null,
    val currentStatus: TrackStatus? = null,

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
