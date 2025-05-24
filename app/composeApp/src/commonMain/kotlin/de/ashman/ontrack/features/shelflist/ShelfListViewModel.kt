package de.ashman.ontrack.features.shelflist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.database.tracking.TrackingRepository
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfListViewModel(
    private val trackingRepository: TrackingRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfListUiState())
    val uiState: StateFlow<ShelfListUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    // TODO load trackings either from other user or current user.
    fun observeTrackings() = viewModelScope.launch {
        trackingRepository.getTrackings().collect { trackings ->
            _uiState.update { it.copy(trackings = trackings) }
        }
    }

    fun updateSelectedStatus(trackStatus: TrackStatus?) {
        _uiState.update {
            it.copy(selectedStatus = trackStatus)
        }
    }

    fun updateSelectedMediaType(mediaType: MediaType) {
        _uiState.update {
            it.copy(selectedMediaType = mediaType)
        }
    }

    fun clearViewModel() {
        _uiState.update { ShelfListUiState() }
    }
}

data class ShelfListUiState(
    val selectedMediaType: MediaType = MediaType.MOVIE,
    val selectedStatus: TrackStatus? = null,
    val trackings: List<NewTracking> = emptyList(),
) {
    val filteredTrackings: List<NewTracking>
        get() = trackings.filter {
            it.media.type == selectedMediaType && (selectedStatus == null || it.status == selectedStatus)
        }
}
