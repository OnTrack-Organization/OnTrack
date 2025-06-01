package de.ashman.ontrack.features.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.network.services.notification.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationService: NotificationService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    init {
        loadNotifications()
    }

    fun loadNotifications() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = NotificationResultState.Loading) }

        notificationService.getLatestNotifications().fold(
            onSuccess = { notifications ->
                _uiState.update {
                    it.copy(
                        notifications = notifications,
                        resultState = if (notifications.isEmpty()) NotificationResultState.Empty else NotificationResultState.Success
                    )
                }

                Logger.d { "Loaded ${notifications.size} notifications: $notifications" }
            },
            onFailure = {
                Logger.e(it) { "Error loading notifications: ${it.message}" }
                _uiState.update { it.copy(resultState = NotificationResultState.Error) }
            }
        )
    }

    fun markAsRead(notificationId: String) {
        // TODO
    }
}

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val resultState: NotificationResultState = NotificationResultState.Empty,
)

enum class NotificationResultState {
    Loading,
    Success,
    Error,
    Empty,
}
