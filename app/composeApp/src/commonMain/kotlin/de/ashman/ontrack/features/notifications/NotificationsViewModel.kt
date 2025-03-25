package de.ashman.ontrack.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NotificationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )
}

data class NotificationsUiState(
    val recentNotifications: List<NotificationData> = emptyList(),
    val resultState: NotificationsResultState = NotificationsResultState.Success,
)

enum class NotificationsResultState {
    Loading,
    Success,
    Error,
    Empty,
}
