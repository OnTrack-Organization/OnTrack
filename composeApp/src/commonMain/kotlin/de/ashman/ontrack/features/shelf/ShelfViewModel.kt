package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.TrackingService
import de.ashman.ontrack.db.entity.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.domain.user.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfViewModel(
    private val trackingService: TrackingService,
    private val authService: AuthService,
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
            authService.consumeUser(userId)
                .collect { user ->
                    _uiState.update { it.copy(user = user?.toDomain()) }
                }
        }
    }

    fun observeUserTrackings(userId: String) {
        trackingService.fetchTrackings(userId)
            .onEach { trackings ->
                _uiState.update {
                    it.copy(trackings = trackings.map { it.toDomain() })
                }
            }
            .launchIn(viewModelScope)
    }
}

data class ShelfUiState(
    val user: User? = null,
    val trackings: List<Tracking> = emptyList(),
)