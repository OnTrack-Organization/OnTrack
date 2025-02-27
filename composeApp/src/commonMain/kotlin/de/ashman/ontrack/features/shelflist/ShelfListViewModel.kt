package de.ashman.ontrack.features.shelflist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.db.TrackingService
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ShelfListViewModel(
    trackingService: TrackingService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfListUiState())
    val uiState: StateFlow<ShelfListUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    val userId = trackingService.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    val trackings = userId
        .filterNotNull()
        .flatMapLatest { id -> trackingService.observeTrackingsForUser(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredTrackings: StateFlow<List<Tracking>> = combine(
        trackings,
        uiState
    ) { trackings, uiState ->
        trackings.filter {
            it.mediaType == uiState.selectedMediaType && (uiState.selectedStatus == null || it.status == uiState.selectedStatus)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    fun updateSelectedTrackType(trackStatus: TrackStatus?) {
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
)
