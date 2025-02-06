package de.ashman.ontrack.features.shelflist

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.FirestoreService
import de.ashman.ontrack.db.toDomain
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.TrackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfListViewModel(
    private val firestoreService: FirestoreService,
    private val authService: AuthService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfListUiState())
    val uiState: StateFlow<ShelfListUiState> = _uiState
        .onEach {
            observeUserTrackings()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    private fun observeUserTrackings() {
        viewModelScope.launch {
            firestoreService.consumeLatestUserTrackings(authService.currentUserId).collect { trackings ->

                val mediaList = trackings
                    .filter { it.status == _uiState.value.selectedStatus }
                    .mapNotNull { firestoreService.getMediaById(it.mediaId)?.toDomain() }
                    .filter { it.mediaType == _uiState.value.selectedMediaType }

                _uiState.update { it.copy(mediaList = mediaList) }
            }
        }
    }

    fun updateSelectedTrackType(trackStatus: TrackStatus) {
        _uiState.update {
            it.copy(selectedStatus = trackStatus)
        }
    }

    fun updateSelectedMediaType(mediaType: MediaType) {
        _uiState.update {
            it.copy(selectedMediaType = mediaType)
        }
    }
}

data class ShelfListUiState(
    val selectedMediaType: MediaType = MediaType.MOVIE,
    val selectedStatus: TrackStatus = TrackStatus.CATALOG,
    val mediaList: List<Media> = emptyList(),
)
