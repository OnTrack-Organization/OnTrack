package de.ashman.ontrack.features.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.services.notification.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notification_mark_all_as_read_error
import ontrack.composeapp.generated.resources.notification_mark_all_as_read_success

class NotificationViewModel(
    private val notificationService: NotificationService,
    private val commonUiManager: CommonUiManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun loadNotifications() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = NotificationResultState.Loading) }

        notificationService.getLatest().fold(
            onSuccess = { notifications ->
                _uiState.update {
                    it.copy(
                        notifications = notifications,
                        hasUnreadNotifications = notifications.any { n -> !n.read },
                        resultState = if (notifications.isEmpty()) NotificationResultState.Empty else NotificationResultState.Success
                    )
                }

                Logger.d { "Loaded ${notifications.size} notifications: $notifications" }
            },
            onFailure = {
                Logger.e(it) { "Error loading notifications: ${it.message}" }
                _uiState.update {
                    it.copy(
                        resultState = NotificationResultState.Error,
                        hasUnreadNotifications = false,
                    )
                }
            }
        )
    }

    fun markAsRead(notificationId: String) = viewModelScope.launch {
        notificationService.markAsRead(notificationId).fold(
            onSuccess = { notification ->
                _uiState.update {
                    val updatedNotifications = it.notifications.map { n ->
                        if (n.id == notificationId) notification else n
                    }

                    it.copy(
                        notifications = updatedNotifications,
                        hasUnreadNotifications = updatedNotifications.any { n -> !n.read }
                    )
                }

                Logger.d { "Marked notification $notificationId as read" }
            },
            onFailure = {
                Logger.e(it) { "Error marking notification $notificationId as read: ${it.message}" }
            }
        )
    }

    fun markAllAsRead() = viewModelScope.launch {
        notificationService.markAllAsRead().fold(
            onSuccess = { updatedNotifications ->
                _uiState.update {
                    it.copy(
                        notifications = updatedNotifications,
                        hasUnreadNotifications = false
                    )
                }

                commonUiManager.showSnackbar(Res.string.notification_mark_all_as_read_success)
                Logger.d { "Marked all notifications as read" }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.notification_mark_all_as_read_error)
                Logger.e(it) { "Failed to mark all as read: ${it.message}" }
            }
        )
    }

    fun clearViewModel() {
        _uiState.update { NotificationUiState() }
    }
}

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val resultState: NotificationResultState = NotificationResultState.Empty,
    val hasUnreadNotifications: Boolean = false,
)

enum class NotificationResultState {
    Loading,
    Success,
    Error,
    Empty,
}
