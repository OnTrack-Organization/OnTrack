package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.TrackingService
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.domain.user.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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

    val userId = trackingService.userId
    val trackings = userId
        .filterNotNull()
        .flatMapLatest { id -> trackingService.observeTrackingsForUser(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    // TODO also move into shared
    fun observeUser(userId: String) {
        viewModelScope.launch {
            authService.observeUser(userId)
                .collect { user ->
                    _uiState.update { it.copy(user = user?.toDomain()) }
                }
        }
    }

    fun setUserId(id: String) {
        trackingService.setUserId(id)
    }

    fun clearViewModel() {
        _uiState.update { ShelfUiState() }
    }
}

data class ShelfUiState(
    val user: User? = null,
)