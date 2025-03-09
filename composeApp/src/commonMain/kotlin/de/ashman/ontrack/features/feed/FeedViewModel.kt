package de.ashman.ontrack.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.domain.feed.Comment
import de.ashman.ontrack.domain.feed.Like
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.firestore.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_comment_body
import ontrack.composeapp.generated.resources.notifications_comment_title
import ontrack.composeapp.generated.resources.notifications_like_body
import ontrack.composeapp.generated.resources.notifications_like_title
import org.jetbrains.compose.resources.getString

class FeedViewModel(
    private val feedRepository: FeedRepository,
    private val notificationService: NotificationService,
    private val currentUserRepository: CurrentUserRepository,
    private val sharedUiManager: SharedUiManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    init {
        viewModelScope.launch {
            currentUserRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun fetchTrackingFeed() = viewModelScope.launch {
        _uiState.update { it.copy(feedResultState = FeedResultState.Loading) }

        feedRepository.getTrackingFeed().collect { feedTrackings ->
            if (feedTrackings.isEmpty()) {
                _uiState.update { it.copy(feedResultState = FeedResultState.Empty) }
            } else {
                _uiState.update { state ->
                    state.copy(
                        feedResultState = FeedResultState.Success,
                        feedTrackings = feedTrackings
                    )
                }
            }
        }
    }

    fun likeTracking(tracking: Tracking) = viewModelScope.launch {
        _uiState.value.user?.let { user ->
            val like = Like(
                userId = user.id,
                username = user.username,
                name = user.name,
                userImageUrl = user.imageUrl,
            )

            if (tracking.isLikedByCurrentUser) {
                feedRepository.unlikeTracking(
                    friendId = tracking.userId,
                    trackingId = tracking.id,
                    like = like
                )
            } else {
                feedRepository.likeTracking(
                    friendId = tracking.userId,
                    trackingId = tracking.id,
                    like = like
                )

                notificationService.sendPushNotification(
                    userId = tracking.userId,
                    title = getString(Res.string.notifications_like_title),
                    body = getString(Res.string.notifications_like_body, like.username),
                )
            }
        }
    }

    fun addComment(comment: String) = viewModelScope.launch {
        _uiState.value.user?.let { user ->
            val newComment = Comment(
                comment = comment,
                userId = user.id,
                username = user.username,
                name = user.name,
                userImageUrl = user.imageUrl,
            )

            _uiState.value.selectedTracking?.let { selectedTracking ->
                feedRepository.addComment(
                    friendId = selectedTracking.userId,
                    trackingId = selectedTracking.id,
                    comment = newComment,
                )

                notificationService.sendPushNotification(
                    userId = selectedTracking.userId,
                    title = getString(Res.string.notifications_comment_title),
                    body = getString(Res.string.notifications_comment_body, newComment.username),
                )
            }
        }
    }

    fun removeComment(comment: Comment) = viewModelScope.launch {
        _uiState.value.selectedTracking?.let { selectedTracking ->
            feedRepository.removeComment(
                friendId = selectedTracking.userId,
                trackingId = selectedTracking.id,
                comment = comment,
            )
        }
    }

    fun selectTrackingAndShowSheet(trackingId: String, currentSheet: CurrentSheet) {
        _uiState.update { it.copy(selectedTrackingId = trackingId) }
        sharedUiManager.showSheet(currentSheet)
    }

    fun clearViewModel() {
        _uiState.update {
            it.copy(
                feedTrackings = emptyList(),
                selectedTrackingId = null,
                feedResultState = FeedResultState.Loading,
                isRefreshing = false,
            )
        }
    }
}

data class FeedUiState(
    val user: User? = null,
    val feedTrackings: List<Tracking> = emptyList(),
    val selectedTrackingId: String? = null,
    val feedResultState: FeedResultState = FeedResultState.Loading,
    val isRefreshing: Boolean = false,
) {
    val selectedTracking: Tracking?
        get() = feedTrackings.find { it.id == selectedTrackingId }
}

enum class FeedResultState {
    Loading,
    Success,
    Empty,
}