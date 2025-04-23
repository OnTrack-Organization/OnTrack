package de.ashman.ontrack.features.share

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.newdomains.NewUser
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.common.CurrentSheet
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.repository.firestore.FirebaseTrackingRepository
import de.ashman.ontrack.repository.firestore.FriendRepository
import de.ashman.ontrack.repository.firestore.ShareRepository
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

class ShareViewModel(
    private val shareRepository: ShareRepository,
    private val firebaseTrackingRepository: FirebaseTrackingRepository,
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
    private val userDataStore: UserDataStore,
    private val commonUiManager: CommonUiManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareUiState())
    val uiState: StateFlow<ShareUiState> = _uiState
        .onStart {
            viewModelScope.launch {
                friendRepository.observeFriends().collect { friends -> _uiState.update { it.copy(friends = friends) } }
            }
            viewModelScope.launch {
                userDataStore.currentUser.collect { user -> _uiState.update { it.copy(user = user) } }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var lastTimestamp: Long? = null
    private val pageSize = 10

    fun fetchInitial() = viewModelScope.launch {
        _uiState.update { it.copy(shareResultState = ShareResultState.Loading, isFirstLaunch = false) }
        delay(1000L)

        // Fetch the first page of the feed
        val firstPage = shareRepository.fetchPage(friendIds = uiState.value.friendIds, pageSize = pageSize)
        lastTimestamp = if (firstPage.isNotEmpty()) firstPage.last().timestamp else null

        // Update the state with the new data from the first page
        _uiState.update {
            it.copy(
                shareResultState = if (firstPage.isEmpty()) ShareResultState.Empty else ShareResultState.Success,
                trackings = firstPage,
                canLoadMore = firstPage.size == pageSize,
            )
        }
    }

    fun fetchNextPage() = viewModelScope.launch {
        if (_uiState.value.shareResultState == ShareResultState.LoadingMore || lastTimestamp == null || !_uiState.value.canLoadMore) return@launch

        _uiState.update { it.copy(shareResultState = ShareResultState.LoadingMore) }
        delay(1000L)

        val nextPage = shareRepository.fetchPage(uiState.value.friendIds, pageSize, lastTimestamp)
        lastTimestamp = if (nextPage.isNotEmpty()) nextPage.last().timestamp else null

        // Append the next page to the feed trackings
        _uiState.update {
            it.copy(
                trackings = it.trackings + nextPage,
                shareResultState = ShareResultState.Success,
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
                userImageUrl = user.profilePictureUrl,
            )

            val updatedTrackings = _uiState.value.trackings.map {
                if (it.id == tracking.id) {
                    if (tracking.isLikedByCurrentUser) {
                        // Unlike case
                        shareRepository.unlikeTracking(tracking.userId, tracking.id, like)
                        it.copy(likes = it.likes - like)
                    } else {
                        // Like case
                        shareRepository.likeTracking(tracking.userId, tracking.id, like)
                        it.copy(likes = it.likes + like)
                    }
                } else it
            }

            _uiState.update { it.copy(trackings = updatedTrackings) }

            if (!tracking.isLikedByCurrentUser) {
                notificationService.sendPushNotification(
                    userId = tracking.userId,
                    title = getString(Res.string.notifications_like_title),
                    body = getString(Res.string.notifications_like_body, like.name),
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
                userImageUrl = user.profilePictureUrl,
            )

            val updatedTrackings = _uiState.value.trackings.map {
                if (it.id == _uiState.value.selectedTrackingId) {
                    shareRepository.addComment(it.userId, it.id, newComment)
                    it.copy(comments = it.comments + newComment)
                } else it
            }

            _uiState.update { it.copy(trackings = updatedTrackings) }

            // Send notification
            notificationService.sendPushNotification(
                userId = _uiState.value.selectedTracking?.userId ?: return@launch,
                title = getString(Res.string.notifications_comment_title),
                body = getString(Res.string.notifications_comment_body, newComment.name),
            )
        }
    }

    fun removeComment(comment: Comment) = viewModelScope.launch {
        val updatedTrackings = _uiState.value.trackings.map {
            if (it.id == _uiState.value.selectedTrackingId) {
                shareRepository.removeComment(it.userId, it.id, comment)
                it.copy(comments = it.comments - comment)
            } else it
        }

        _uiState.update { it.copy(trackings = updatedTrackings) }
    }

    fun selectTrackingAndShowSheet(userId: String, trackingId: String, currentSheet: CurrentSheet) {
        _uiState.update { it.copy(selectedTrackingId = trackingId) }

        viewModelScope.launch {
            refreshTracking(userId = userId, trackingId = trackingId)
            commonUiManager.showSheet(currentSheet)
        }
    }

    private fun refreshTracking(userId: String, trackingId: String) = viewModelScope.launch {
        val refreshedTracking = firebaseTrackingRepository.fetchTrackingById(userId = userId, trackingId = trackingId)
        if (refreshedTracking != null) {
            _uiState.update { state ->
                state.copy(
                    trackings = state.trackings.map {
                        if (it.id == trackingId) refreshedTracking else it
                    }
                )
            }
        }
    }

    fun updateHasNewNotifications(hasNewNotifications: Boolean) {
        _uiState.update { it.copy(hasNewNotifications = hasNewNotifications) }
    }

    fun clearViewModel() {
        _uiState.update {
            it.copy(
                trackings = emptyList(),
                selectedTrackingId = null,
                shareResultState = ShareResultState.Empty,
            )
        }
    }
}

data class ShareUiState(
    val user: NewUser? = null,
    val friends: List<Friend> = emptyList(),
    val trackings: List<Tracking> = emptyList(),
    val selectedTrackingId: String? = null,
    val shareResultState: ShareResultState = ShareResultState.Loading,
    val canLoadMore: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val listState: LazyListState = LazyListState(0, 0),
    val hasNewNotifications: Boolean = false,
) {
    val selectedTracking: Tracking?
        get() = trackings.find { it.id == selectedTrackingId }

    val friendIds: List<String>
        get() = listOfNotNull(user?.id) + friends.map { it.id }
}

enum class ShareResultState {
    Loading,
    Success,
    Empty,
    LoadingMore,
}