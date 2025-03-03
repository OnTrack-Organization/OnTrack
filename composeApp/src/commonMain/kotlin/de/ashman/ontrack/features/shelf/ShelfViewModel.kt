package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.db.AuthRepository
import de.ashman.ontrack.db.TrackingRepository
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfViewModel(
    private val trackingRepository: TrackingRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    fun observeUser(userId: String) {
        viewModelScope.launch {
            authRepository.observeUser(userId)
                .collect { user ->
                    _uiState.update { it.copy(user = user) }
                }
        }
    }

    fun observeUserTrackings(userId: String) {
        trackingRepository.fetchTrackings(userId)
            .onEach { trackings -> _uiState.update { it.copy(trackings = trackings) } }
            .launchIn(viewModelScope)
    }

    fun clearViewModel() {
        _uiState.update { ShelfUiState() }
    }
}

data class ShelfUiState(
    val user: User? = null,
    val trackings: List<Tracking> = emptyList(),
)