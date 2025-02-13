package de.ashman.ontrack.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.db.FirestoreService
import de.ashman.ontrack.db.toDomain
import de.ashman.ontrack.db.toEntity
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.tracking.TrackingComment
import de.ashman.ontrack.domain.tracking.TrackingLike
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val firestoreService: FirestoreService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState
        .onStart {
            fetchTrackingFeed()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var lastTimestamp: Long? = null

    fun fetchTrackingFeed() = viewModelScope.launch {
        _uiState.update { it.copy(feedResultState = FeedResultState.Loading) }

        firestoreService.getTrackingFeed(lastTimestamp = lastTimestamp, limit = 10).collect { feedTrackings ->
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
        val like = TrackingLike()

        if (tracking.isLikedByCurrentUser) {
            firestoreService.unlikeTracking(
                friendId = tracking.userId,
                trackingId = tracking.id,
                like = like.toEntity()
            )
        } else {
            firestoreService.likeTracking(
                friendId = tracking.userId,
                trackingId = tracking.id,
                like = like.toEntity()
            )
        }
    }

    fun addComment(comment: String) = viewModelScope.launch {
        val newComment = TrackingComment(comment = comment)

        _uiState.value.selectedTracking?.let { selectedTracking ->
            firestoreService.addComment(
                friendId = selectedTracking.userId,
                trackingId = selectedTracking.id,
                comment = newComment.toEntity(),
            )
        }
    }

    fun deleteComment(comment: TrackingComment) = viewModelScope.launch {
        _uiState.value.selectedTracking?.let { selectedTracking ->
            firestoreService.deleteComment(
                friendId = selectedTracking.userId,
                trackingId = selectedTracking.id,
                comment = comment.toEntity(),
            )
        }
    }

    fun selectTracking(trackingId: String) {
        _uiState.update { it.copy(selectedTrackingId = trackingId) }
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