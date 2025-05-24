package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.database.tracking.TrackingRepository
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.tracking.NewTracking
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.services.friend.FriendService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.friend_accept_request_error
import ontrack.composeapp.generated.resources.friend_cancel_request_error
import ontrack.composeapp.generated.resources.friend_decline_request_error
import ontrack.composeapp.generated.resources.friend_delete_error
import ontrack.composeapp.generated.resources.friend_send_request_error
import ontrack.composeapp.generated.resources.shelf_get_profile_error

class ShelfViewModel(
    private val userDataStore: UserDataStore,
    private val trackingRepository: TrackingRepository,
    private val friendService: FriendService,
    private val commonUiManager: CommonUiManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    fun loadUserProfile(userId: String) = viewModelScope.launch {
        val currentUser = userDataStore.getCurrentUser()
        val isCurrentUser = userId == currentUser.id

        if (isCurrentUser) {
            _uiState.update {
                it.copy(
                    user = currentUser,
                    friendStatus = null,
                )
            }
            observeTrackings()
        } else {
            friendService.getUserProfile(userId).fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            user = profile.user.user,
                            friendStatus = profile.user.friendStatus,
                            trackings = profile.trackings
                        )
                    }
                },
                onFailure = {
                    commonUiManager.showSnackbar(Res.string.shelf_get_profile_error)
                }
            )
        }
    }

    fun observeTrackings() = viewModelScope.launch {
        trackingRepository.getTrackings().collect { trackings ->
            _uiState.update { it.copy(trackings = trackings) }
        }
    }

    fun sendRequest() = viewModelScope.launch {
        friendService.sendRequest(uiState.value.user?.id.orEmpty()).fold(
            onSuccess = {
                _uiState.update { it.copy(friendStatus = FriendStatus.REQUEST_SENT) }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_send_request_error)
            }
        )
    }

    fun acceptRequest() = viewModelScope.launch {
        friendService.acceptRequest(uiState.value.user?.id.orEmpty()).fold(
            onSuccess = {
                _uiState.update { it.copy(friendStatus = FriendStatus.FRIEND) }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_accept_request_error)
            }
        )
    }

    fun declineRequest() = viewModelScope.launch {
        friendService.declineRequest(uiState.value.user?.id.orEmpty()).fold(
            onSuccess = {
                _uiState.update { it.copy(friendStatus = FriendStatus.STRANGER) }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_decline_request_error)
            }
        )
    }

    fun cancelRequest() = viewModelScope.launch {
        friendService.cancelRequest(uiState.value.user?.id.orEmpty()).fold(
            onSuccess = {
                _uiState.update { it.copy(friendStatus = FriendStatus.STRANGER) }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_cancel_request_error)
            }
        )
    }

    fun removeFriend() = viewModelScope.launch {
        friendService.deleteFriend(uiState.value.user?.id.orEmpty()).fold(
            onSuccess = {
                _uiState.update { it.copy(friendStatus = FriendStatus.STRANGER) }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_delete_error)
            }
        )
    }

    fun clearViewModel() {
        _uiState.update { ShelfUiState() }
    }
}

data class ShelfUiState(
    val user: User? = null,
    val friendStatus: FriendStatus? = null,
    val trackings: List<NewTracking> = emptyList(),
)