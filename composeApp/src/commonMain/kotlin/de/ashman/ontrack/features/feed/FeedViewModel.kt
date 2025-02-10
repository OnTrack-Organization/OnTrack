package de.ashman.ontrack.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.FirestoreService
import de.ashman.ontrack.db.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val firestoreService: FirestoreService,
    private val authService: AuthService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun fetchTrackingFeed() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(feedResultState = FeedResultState.Loading)
        val feedTrackings = firestoreService.getTrackingFeed()

        if (feedTrackings.isEmpty()) {
            _uiState.value = _uiState.value.copy(feedResultState = FeedResultState.Empty)
        } else {
            _uiState.value = _uiState.value.copy(feedResultState = FeedResultState.Success)
        }

        _uiState.value = _uiState.value.copy(feedTrackings = feedTrackings.map { it.toDomain() })
    }

    fun likeTracking(tracking: Tracking) = viewModelScope.launch {
        tracking.userId?.let {
            if (isLikedByCurrentUser(tracking)) {
                firestoreService.unlikeTracking(it, tracking.id)
            } else {
                firestoreService.likeTracking(it, tracking.id)
            }
        }
    }

    fun addComment(comment: String) = viewModelScope.launch {
        _uiState.value.selectedTracking?.let {
            firestoreService.addComment(friendId = it.userId.orEmpty(), trackingId = it.id, comment = comment)
        }
    }

    fun deleteComment(commentId: String) = viewModelScope.launch {
        _uiState.value.selectedTracking?.let {
            firestoreService.deleteComment(friendId = it.userId.orEmpty(), trackingId = it.id, commentId = commentId)
        }
    }

    fun selectTracking(tracking: Tracking) {
        _uiState.update { it.copy(selectedTracking = tracking) }
    }

    fun isLikedByCurrentUser(tracking: Tracking) = tracking.likedBy.contains(authService.currentUserId)
}

data class FeedUiState(
    val selectedTracking: Tracking? = null,
    val feedTrackings: List<Tracking> = emptyList(),
    val feedResultState: FeedResultState = FeedResultState.Loading,
    val isRefreshing: Boolean = false,
)

enum class FeedResultState {
    Loading,
    Success,
    Empty,
}