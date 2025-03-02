package de.ashman.ontrack.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.db.FeedRepository
import de.ashman.ontrack.domain.feed.Comment
import de.ashman.ontrack.domain.feed.Like
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var lastTimestamp: Long? = null

    fun fetchTrackingFeed() = viewModelScope.launch {
        _uiState.update { it.copy(feedResultState = FeedResultState.Loading) }

        feedRepository.getTrackingFeed(lastTimestamp = lastTimestamp, limit = 10).collect { feedTrackings ->
            if (feedTrackings.isEmpty()) {
                _uiState.update { it.copy(feedResultState = FeedResultState.Empty) }
            } else {
                lastTimestamp = feedTrackings.last().timestamp
                _uiState.update { state ->
                    state.copy(
                        feedResultState = FeedResultState.Success,
                        feedTrackings = feedTrackings.map { it.toDomain() }
                    )
                }
            }
        }
    }

    fun likeTracking(tracking: Tracking) = viewModelScope.launch {
        val like = Like()

        if (tracking.isLikedByCurrentUser) {
            feedRepository.unlikeTracking(
                friendId = tracking.userId,
                trackingId = tracking.id,
                like = like.toEntity()
            )
        } else {
            feedRepository.likeTracking(
                friendId = tracking.userId,
                trackingId = tracking.id,
                like = like.toEntity()
            )
        }
    }

    fun addComment(comment: String) = viewModelScope.launch {
        val newComment = Comment(comment = comment)

        _uiState.value.selectedTracking?.let { selectedTracking ->
            feedRepository.addComment(
                friendId = selectedTracking.userId,
                trackingId = selectedTracking.id,
                comment = newComment.toEntity(),
            )
        }
    }

    fun removeComment(comment: Comment) = viewModelScope.launch {
        _uiState.value.selectedTracking?.let { selectedTracking ->
            feedRepository.removeComment(
                friendId = selectedTracking.userId,
                trackingId = selectedTracking.id,
                comment = comment.toEntity(),
            )
        }
    }

    fun selectTracking(trackingId: String) {
        _uiState.update { it.copy(selectedTrackingId = trackingId) }
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