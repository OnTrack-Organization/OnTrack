package de.ashman.ontrack.features.shelflist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.repository.firestore.TrackingRepository
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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

    fun observeUserTrackings(userId: String) {
        trackingRepository.observeTrackings(userId)
            .onEach { trackings -> _uiState.update { it.copy(trackings = trackings) } }
            .launchIn(viewModelScope)
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
    val trackings: List<Tracking> = emptyList(),
) {
    val filteredTrackings: List<Tracking>
        get() = trackings.filter {
            it.mediaType == selectedMediaType && (selectedStatus == null || it.status == selectedStatus)
        }
}
