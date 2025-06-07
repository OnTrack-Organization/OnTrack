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
import de.ashman.ontrack.database.review.ReviewRepository
import de.ashman.ontrack.database.tracking.TrackingRepository
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.media.toDto
import de.ashman.ontrack.domain.recommendation.FriendsActivity
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.network.services.friend.FriendService
import de.ashman.ontrack.network.services.recommendation.RecommendationService
import de.ashman.ontrack.network.services.recommendation.dto.CreateRecommendationDto
import de.ashman.ontrack.network.services.review.ReviewService
import de.ashman.ontrack.network.services.review.dto.CreateReviewDto
import de.ashman.ontrack.network.services.tracking.TrackingService
import de.ashman.ontrack.network.services.tracking.dto.CreateTrackingDto
import de.ashman.ontrack.network.services.tracking.dto.UpdateTrackingDto
import de.ashman.ontrack.repository.SelectedMediaRepository
import de.ashman.ontrack.util.getSingularTitle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
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
    private val reviewRepository: ReviewRepository,
    private val trackingService: TrackingService,
    private val reviewService: ReviewService,
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

    private var trackingJob: Job? = null

    init {
        viewModelScope.launch {
            selectedMediaRepository.selectedMedia.collect { media ->
                _uiState.update { it.copy(media = media) }
            }
        }
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

    fun observeTrackingAndReview(mediaId: String, mediaType: MediaType) {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            trackingRepository.getTracking(mediaId, mediaType)
                .onEach { tracking ->
                    _uiState.update {
                        it.copy(
                            tracking = tracking,
                            status = tracking?.status,
                            review = null // reset review until it's reloaded
                        )
                    }
                }
                .flatMapLatest { tracking ->
                    tracking?.let { reviewRepository.getReview(it.id) } ?: flowOf(null)
                }
                .collect { review ->
                    _uiState.update { it.copy(review = review) }
                }
        }
    }

    fun saveChanges(status: TrackStatus? = null) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        saveOrUpdateTracking(status)?.let { tracking ->
            commonUiManager.hideSheetAndShowSnackbar(
                getString(
                    resource = Res.string.tracking_saved,
                    tracking.media.type.getSingularTitle(),
                    getString(tracking.status.getLabel(tracking.media.type)),
                )
            )

            _uiState.update {
                it.copy(
                    resultState = DetailResultState.Success
                )
            }

            Logger.d { "Tracking saved: $tracking" }
        }
    }

    fun saveChangesWithReview(rating: Double, title: String?, description: String?) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        val tracking = saveOrUpdateTracking()
        if (tracking == null) return@launch

        val reviewDto = CreateReviewDto(
            trackingId = tracking.id,
            rating = rating,
            title = title,
            description = description
        )

        val reviewResult = if (_uiState.value.review == null) {
            reviewService.createReview(reviewDto)
        } else {
            reviewService.updateReview(reviewDto)
        }

        reviewResult.fold(
            onSuccess = { review ->
                reviewRepository.addReview(review)

                commonUiManager.hideSheetAndShowSnackbar(
                    getString(
                        resource = Res.string.tracking_saved,
                        tracking.media.type.getSingularTitle(),
                        getString(tracking.status.getLabel(tracking.media.type)),
                    )
                )

                _uiState.update {
                    it.copy(
                        resultState = DetailResultState.Success
                    )
                }

                Logger.d { "Review saved: $review" }
            },
            onFailure = { e ->
                _uiState.update { it.copy(resultState = DetailResultState.Error) }
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_update_error))
                Logger.e { "Failed to save review: ${e.message}" }
            }
        )
    }

    private suspend fun saveOrUpdateTracking(status: TrackStatus? = null): Tracking? {
        val result = if (_uiState.value.tracking == null) {
            val dto = CreateTrackingDto(
                media = _uiState.value.media!!.toDto(),
                status = status ?: _uiState.value.status!!
            )
            trackingService.createTracking(dto)
        } else {
            val dto = UpdateTrackingDto(
                id = _uiState.value.tracking!!.id,
                status = _uiState.value.status!!
            )
            trackingService.updateTracking(dto)
        }

        return result.fold(
            onSuccess = { tracking ->
                trackingRepository.addTracking(tracking)
                tracking
            },
            onFailure = { e ->
                _uiState.update { it.copy(resultState = DetailResultState.Error) }
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.tracking_create_error))
                Logger.e { "Failed to save tracking: ${e.message}" }
                null
            }
        )
    }

    fun removeTracking() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = DetailResultState.Loading) }

        val tracking = _uiState.value.tracking ?: return@launch

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
                _uiState.update { it.copy(resultState = DetailResultState.Error) }
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
            media = _uiState.value.media!!.toDto(),
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

    fun selectStatus(status: TrackStatus?) {
        _uiState.update { it.copy(status = status) }
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
    val media: Media? = null,
    val tracking: Tracking? = null,
    val status: TrackStatus? = null,

    val review: Review? = null,

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