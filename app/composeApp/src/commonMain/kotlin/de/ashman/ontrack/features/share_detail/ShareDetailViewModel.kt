package de.ashman.ontrack.features.share_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock.System

class ShareDetailViewModel(
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShareDetailUiState())
    val uiState: StateFlow<ShareDetailUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun fetchTracking(trackingId: String) {
        // TODO
        _uiState.value = ShareDetailUiState(
            tracking = Tracking(
                id = trackingId,
                userId = "1",
                username = "Ashkan",
                userImageUrl = "",
                mediaId = "1",
                mediaType = MediaType.MOVIE,
                mediaTitle = "The Shawshank Redemption",
                mediaCoverUrl = "",
                status = TrackStatus.CONSUMED,
                rating = 5.0,
                reviewTitle = "Great movie",
                reviewDescription = "This movie is great",
                timestamp = System.now().toEpochMilliseconds(),
                likes = listOf(),
                comments = listOf(),
            )
        )
    }
}

data class ShareDetailUiState(
    val tracking: Tracking? = null,
)
