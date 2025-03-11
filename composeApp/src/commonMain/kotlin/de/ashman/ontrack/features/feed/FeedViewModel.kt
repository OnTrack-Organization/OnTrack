package de.ashman.ontrack.features.feed

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.domain.feed.Comment
import de.ashman.ontrack.domain.feed.Like
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.firestore.FeedRepository
import de.ashman.ontrack.repository.firestore.FriendRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
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
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
    private val currentUserRepository: CurrentUserRepository,
    private val sharedUiManager: SharedUiManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState
        .onStart {
            viewModelScope.launch {
                friendRepository.observeFriends().collect { friends -> _uiState.update { it.copy(friends = friends) } }
            }
            viewModelScope.launch {
                currentUserRepository.currentUser.collect { user -> _uiState.update { it.copy(user = user) } }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var lastTimestamp: Long? = null
    private val pageSize = 10

    fun fetchInitialTrackingFeed() = viewModelScope.launch {
        _uiState.update { it.copy(feedResultState = FeedResultState.Loading, isFirstLaunch = false) }
        delay(1000L)

        // Fetch the first page of the feed
        val firstPage = feedRepository.fetchTrackingFeedPage(friendIds = uiState.value.friendIds, pageSize = pageSize)
        lastTimestamp = if (firstPage.isNotEmpty()) firstPage.last().timestamp else null

        // Update the state with the new data from the first page
        _uiState.update {
            it.copy(
                feedResultState = if (firstPage.isEmpty()) FeedResultState.Empty else FeedResultState.Success,
                feedTrackings = firstPage,
                canLoadMore = firstPage.size == pageSize,
            )
        }
    }

    fun fetchNextPage() = viewModelScope.launch {
        if (_uiState.value.feedResultState == FeedResultState.LoadingMore || lastTimestamp == null || !_uiState.value.canLoadMore) return@launch

        _uiState.update { it.copy(feedResultState = FeedResultState.LoadingMore) }
        delay(1000L)

        val nextPage = feedRepository.fetchTrackingFeedPage(uiState.value.friendIds, pageSize, lastTimestamp)
        lastTimestamp = if (nextPage.isNotEmpty()) nextPage.last().timestamp else null

        // Append the next page to the feed trackings
        _uiState.update {
            it.copy(
                feedTrackings = it.feedTrackings + nextPage,
                feedResultState = FeedResultState.Success,
                canLoadMore = nextPage.size == pageSize
            )
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

            val updatedTrackings = _uiState.value.feedTrackings.map {
                if (it.id == tracking.id) {
                    if (tracking.isLikedByCurrentUser) {
                        // Unlike case
                        feedRepository.unlikeTracking(tracking.userId, tracking.id, like)
                        it.copy(likes = it.likes - like)
                    } else {
                        // Like case
                        feedRepository.likeTracking(tracking.userId, tracking.id, like)
                        it.copy(likes = it.likes + like)
                    }
                } else it
            }

            _uiState.update { it.copy(feedTrackings = updatedTrackings) }

            if (!tracking.isLikedByCurrentUser) {
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

            val updatedTrackings = _uiState.value.feedTrackings.map {
                if (it.id == _uiState.value.selectedTrackingId) {
                    feedRepository.addComment(it.userId, it.id, newComment)
                    it.copy(comments = it.comments + newComment)
                } else it
            }

            _uiState.update { it.copy(feedTrackings = updatedTrackings) }

            // Send notification
            notificationService.sendPushNotification(
                userId = _uiState.value.selectedTracking?.userId ?: return@launch,
                title = getString(Res.string.notifications_comment_title),
                body = getString(Res.string.notifications_comment_body, newComment.username),
            )
        }
    }

    fun removeComment(comment: Comment) = viewModelScope.launch {
        val updatedTrackings = _uiState.value.feedTrackings.map {
            if (it.id == _uiState.value.selectedTrackingId) {
                feedRepository.removeComment(it.userId, it.id, comment)
                it.copy(comments = it.comments - comment)
            } else it
        }

        _uiState.update { it.copy(feedTrackings = updatedTrackings) }
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
                feedResultState = FeedResultState.Empty,
            )
        }
    }
}

data class FeedUiState(
    val user: User? = null,
    val friends: List<Friend> = emptyList(),
    val feedTrackings: List<Tracking> = emptyList(),
    val selectedTrackingId: String? = null,
    val feedResultState: FeedResultState = FeedResultState.Loading,
    val canLoadMore: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val listState: LazyListState = LazyListState(0, 0),
) {
    val selectedTracking: Tracking?
        get() = feedTrackings.find { it.id == selectedTrackingId }

    val friendIds: List<String>
        get() = listOfNotNull(user?.id) + friends.map { it.id }
}

enum class FeedResultState {
    Loading,
    Success,
    Empty,
    LoadingMore,
}