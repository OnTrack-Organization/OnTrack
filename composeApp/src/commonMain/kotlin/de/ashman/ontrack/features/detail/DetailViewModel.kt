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
import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.recommendation.RecommendationStatus
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.notification.NotificationService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_new_recommendation_body
import ontrack.composeapp.generated.resources.notifications_new_recommendation_title
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
    private val authRepository: AuthRepository,
    private val recommendationRepository: RecommendationRepository,
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState
        .onStart { observeUser() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _uiState.value,
        )

    // TODO move recommendation stuff into its own viewmodel or use cases
    private val previousRecommendationsCache = mutableMapOf<String, List<Recommendation>>()

    fun fetchDetails(mediaNavItems: MediaNavigationItems) = viewModelScope.launch {
        measureTime {
            _uiState.update {
                it.copy(
                    resultState = DetailResultState.Loading,
                    selectedMedia = null,
                    ratingStats = RatingStats(),
                )
            }

            getRepository(mediaNavItems.mediaType).fetchDetails(mediaNavItems.id).fold(
                onSuccess = { media ->
                    _uiState.update {
                        it.copy(
                            resultState = DetailResultState.Success,
                            selectedMedia = media,
                        )
                    }
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
        trackingRepository.saveTracking(tracking)

        _uiState.update { it.copy(selectedTracking = tracking) }
    }

    fun removeTracking(trackingId: String) = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch

        trackingRepository.removeTracking(trackingId, "${media.mediaType}_${media.id}")

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
    }

    fun passRecommendation() = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch
        recommendationRepository.passRecommendation(media.id)
    }

    fun sendRecommendation(friendId: String, message: String?) = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch

        _uiState.value.user?.let { user ->
            val recommendation = Recommendation(
                userId = user.id,
                userImageUrl = user.imageUrl,
                username = user.name,
                mediaId = media.id,
                mediaType = media.mediaType,
                mediaTitle = media.title,
                mediaCoverUrl = media.coverUrl,
                message = message,
                status = RecommendationStatus.PENDING
            )

            recommendationRepository.sendRecommendation(friendId, recommendation)

            // UPDATE THE PREVIOUS SENT CACHE
            val updatedRecs = listOf(recommendation) + (previousRecommendationsCache[friendId] ?: emptyList()).take(4)
            previousRecommendationsCache[friendId] = updatedRecs
            _uiState.update { it.copy(previousSentRecommendations = updatedRecs) }

            notificationService.sendPushNotification(
                userId = friendId,
                title = getString(Res.string.notifications_new_recommendation_title),
                body = getString(Res.string.notifications_new_recommendation_body, user.name, media.title),
                mediaId = media.id,
                imageUrl = media.coverUrl,
            )
        }
    }


    fun fetchFriends() = viewModelScope.launch {
        friendRepository.getFriends().collect { friends ->
            _uiState.update { it.copy(friends = friends) }
        }
    }

    fun observeUser() {
        viewModelScope.launch {
            authRepository.observeUser(Firebase.auth.currentUser?.uid.orEmpty())
                .collect { user ->
                    _uiState.update { it.copy(user = user) }
                }
        }
    }

    fun clearViewModel() {
        _uiState.update { DetailUiState() }
    }

    fun observeRatingStats(mediaId: String, mediaType: MediaType) = viewModelScope.launch {
        trackingRepository.observeRatingStats("${mediaType}_$mediaId").collectLatest { ratingStats ->
            ratingStats?.let {
                _uiState.update { it.copy(ratingStats = ratingStats) }
            }
        }
    }

    fun selectUser(friendId: String) {
        val cachedRecs = previousRecommendationsCache[friendId]
        if (cachedRecs != null) {
            _uiState.update { it.copy(previousSentRecommendations = cachedRecs) }
        } else {
            getPreviousSentRecommendations(friendId)
        }
    }

    fun getPreviousSentRecommendations(friendId: String) = viewModelScope.launch {
        val media = _uiState.value.selectedMedia ?: return@launch

        val recs = recommendationRepository.getPreviousSentRecommendations(friendId, media.id)
        previousRecommendationsCache[friendId] = recs

        _uiState.update { it.copy(previousSentRecommendations = recs) }
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
    val friends: List<Friend> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
    val previousSentRecommendations: List<Recommendation> = emptyList(),
    val resultState: DetailResultState = DetailResultState.Loading,
    val isRefreshing: Boolean = false,
    val searchDuration: Long = 0L,
)

enum class DetailResultState {
    Loading,
    Success,
    Error,
}
