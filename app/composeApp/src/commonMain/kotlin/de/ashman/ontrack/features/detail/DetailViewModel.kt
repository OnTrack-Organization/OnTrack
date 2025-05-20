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
import de.ashman.ontrack.database.TrackingRepository
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.media.toDto
import de.ashman.ontrack.domain.recommendation.FriendsActivity
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.network.services.friend.FriendService
import de.ashman.ontrack.network.services.recommendation.RecommendationService
import de.ashman.ontrack.network.services.recommendation.dto.CreateRecommendationDto
import de.ashman.ontrack.network.services.tracking.TrackingService
import de.ashman.ontrack.network.services.tracking.dto.CreateTrackingDto
import de.ashman.ontrack.network.services.tracking.dto.UpdateTrackingDto
import de.ashman.ontrack.repository.SelectedMediaRepository
import de.ashman.ontrack.util.getSingularTitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.recommendation_send
import ontrack.composeapp.generated.resources.recommendation_send_error
import ontrack.composeapp.generated.resources.tracking_create_error
import ontrack.composeapp.generated.resources.tracking_delete_error
import ontrack.composeapp.generated.resources.tracking_removed
import ontrack.composeapp.generated.resources.tracking_saved
import ontrack.composeapp.generated.resources.tracking_update_error
import org.jetbrains.compose.resources.getString

class DetailViewModel(
    private val commonUiManager: CommonUiManager,
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val bookRepository: BookRepository,
    private val videogameRepository: VideogameRepository,
    private val boardgameRepository: BoardgameRepository,
    private val albumRepository: AlbumRepository,
    private val selectedMediaRepository: SelectedMediaRepository,
    private val trackingRepository: TrackingRepository,
    private val trackingService: TrackingService,
    private val friendService: FriendService,
    private val recommendationService: RecommendationService,
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

    fun observeTracking(mediaId: String, mediaType: MediaType) = viewModelScope.launch {
        trackingRepository.getTracking(mediaId, mediaType).collect { tracking ->
            _uiState.update { it.copy(currentTracking = tracking, currentStatus = tracking?.status) }
        }
    }

    fun observeRatingStats(mediaId: String, mediaType: MediaType) = viewModelScope.launch {
        /*firebaseTrackingRepository.observeRatingStats("${mediaType}_$mediaId").collectLatest { ratingStats ->
            ratingStats?.let {
                _uiState.update { it.copy(ratingStats = ratingStats) }
            }
        }*/
    }

    fun fetchDetails(mediaNav: MediaNavigationParam) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                apiResultState = ApiResultState.ApiLoading,
                ratingStats = RatingStats(),
            )
        }

        getRepository(mediaNav.type).fetchDetails(mediaNav.id).fold(
            onSuccess = { media ->
                _uiState.update { it.copy(apiResultState = ApiResultState.ApiSuccess) }
                selectedMediaRepository.selectMedia(media)
                Logger.d { "Fetched details: $media" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(apiResultState = ApiResultState.ApiError) }
                Logger.e { "Failed to fetch details: ${exception.message}" }
            }
        )
    }

    fun selectStatus(status: TrackStatus?) {
        _uiState.update { it.copy(currentStatus = status) }
    }

    fun saveTracking() {
        if (_uiState.value.currentTracking == null) {
            val dto = CreateTrackingDto(
                media = _uiState.value.currentMedia!!.toDto(),
                status = _uiState.value.currentStatus!!,
            )

            createTracking(dto)
        } else {
            updateTracking()
        }
    }

    private fun createTracking(dto: CreateTrackingDto) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        trackingService.createTracking(dto).fold(
            onSuccess = { tracking ->
                commonUiManager.hideSheetAndShowSnackbar(
                    getString(
                        resource = Res.string.tracking_saved,
                        tracking.media.type.getSingularTitle(),
                        getString(tracking.status.getLabel(tracking.media.type)),
                    )
                )

                trackingRepository.addTracking(tracking)

                _uiState.update { it.copy(resultState = DetailResultState.Success) }

                Logger.d { "New tracking created: $tracking" }
            },
            onFailure = { exception ->
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_create_error))
                Logger.e { "Failed to create tracking: ${exception.message}" }
            }
        )
    }

    private fun updateTracking() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        val dto = UpdateTrackingDto(
            id = _uiState.value.currentTracking!!.id,
            status = _uiState.value.currentStatus!!,
        )

        trackingService.updateTracking(dto).fold(
            onSuccess = { tracking ->
                commonUiManager.hideSheetAndShowSnackbar(
                    getString(
                        resource = Res.string.tracking_saved,
                        tracking.media.type.getSingularTitle(),
                        getString(tracking.status.getLabel(tracking.media.type)),
                    )
                )

                trackingRepository.addTracking(tracking)

                _uiState.update { it.copy(resultState = DetailResultState.Success) }

                Logger.d { "Tracking updated: $tracking" }
            },
            onFailure = { exception ->
                // TODO DONT HIDE, USE TOAST INSTEAD
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_update_error))
                Logger.e { "Failed to update tracking: ${exception.message}" }
            }
        )
    }

    fun removeTracking() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

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

                trackingRepository.deleteTracking(tracking.id)

                _uiState.update { it.copy(resultState = DetailResultState.Success) }

                Logger.d { "Tracking removed: $tracking" }
            },
            onFailure = { exception ->
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_delete_error))
                Logger.e { "Failed to remove tracking: ${exception.message}" }
            }
        )
    }

    fun fetchFriends() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        friendService.getFriends().fold(
            onSuccess = { users ->
                _uiState.update {
                    it.copy(
                        resultState = DetailResultState.Success,
                        friends = users,
                    )
                }
                Logger.d { "Fetched friends: $users" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(resultState = DetailResultState.Error) }
                Logger.e { "Failed to fetch friends: ${exception.message}" }
            }
        )
    }

    fun sendRecommendation(userId: String, message: String?) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        val dto = CreateRecommendationDto(
            userId = userId,
            media = _uiState.value.currentMedia!!.toDto(),
            message = message,
        )

        recommendationService.sendRecommendation(dto).fold(
            onSuccess = {
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.recommendation_send))
                _uiState.update { it.copy(resultState = DetailResultState.Success) }
                Logger.d { "Recommendation sent to user: $userId" }
            },
            onFailure = { exception ->
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.recommendation_send_error))
                Logger.e { "Failed to send recommendation to user: $userId, ${exception.message}" }
            }
        )
    }

    fun fetchSentRecommendations(mediaType: MediaType, mediaId: String, userId: String) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        recommendationService.getSentRecommendations(mediaType, mediaId, userId).fold(
            onSuccess = { sentRecommendations ->
                _uiState.update {
                    it.copy(
                        sentRecommendations = sentRecommendations,
                        resultState = DetailResultState.Success,
                    )
                }
                Logger.d { "Fetched sent recommendations: $sentRecommendations" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(resultState = DetailResultState.Error) }
                Logger.e { "Failed to fetch sent recommendations: ${exception.message}" }
            }
        )
    }

    fun fetchFriendsActivity(mediaType: MediaType, mediaId: String) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        recommendationService.getFriendsActivity(mediaType, mediaId).fold(
            onSuccess = { friendsActivity ->
                _uiState.update {
                    it.copy(
                        friendsActivity = friendsActivity,
                        resultState = DetailResultState.Success,
                    )
                }
                Logger.d { "Fetched friends activity: $friendsActivity" }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(resultState = DetailResultState.Error) }
                Logger.e { "Failed to fetch friends activity: ${exception.message}" }
            }
        )
    }

    fun catalogRecommendation() {
        val dto = CreateTrackingDto(
            media = _uiState.value.currentMedia!!.toDto(),
            status = TrackStatus.CATALOG,
        )

        createTracking(dto)
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

    val friends: List<User> = emptyList(),
    val friendsActivity: FriendsActivity? = null,

    val sentRecommendations: List<Recommendation> = emptyList(),

    val apiResultState: ApiResultState = ApiResultState.ApiLoading,
    val resultState: DetailResultState = DetailResultState.Loading,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}

enum class ApiResultState {
    ApiLoading,
    ApiSuccess,
    ApiError,
}