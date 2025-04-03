package de.ashman.ontrack.features.share_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

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
    }
}

data class ShareDetailUiState(
    val tracking: Tracking? = null,
)
