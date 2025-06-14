package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.database.tracking.TrackingRepository
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.services.block.BlockService
import de.ashman.ontrack.network.services.friend.FriendService
import de.ashman.ontrack.network.services.report.ReportService
import de.ashman.ontrack.network.services.report.dto.ReportReason
import de.ashman.ontrack.network.services.report.dto.ReportRequestDto
import de.ashman.ontrack.network.services.report.dto.ReportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.block_error
import ontrack.composeapp.generated.resources.block_success
import ontrack.composeapp.generated.resources.friend_accept_request_error
import ontrack.composeapp.generated.resources.friend_cancel_request_error
import ontrack.composeapp.generated.resources.friend_decline_request_error
import ontrack.composeapp.generated.resources.friend_delete_error
import ontrack.composeapp.generated.resources.friend_send_request_error
import ontrack.composeapp.generated.resources.report_error
import ontrack.composeapp.generated.resources.report_success
import ontrack.composeapp.generated.resources.shelf_get_profile_error
import ontrack.composeapp.generated.resources.unblock_error
import ontrack.composeapp.generated.resources.unblock_success

class ShelfViewModel(
    private val userDataStore: UserDataStore,
    private val trackingRepository: TrackingRepository,
    private val friendService: FriendService,
    private val blockService: BlockService,
    private val reportService: ReportService,
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

    fun loadUserProfile(userId: String?) = viewModelScope.launch {
        val currentUser = userDataStore.getCurrentUser()
        // If userId is null, treat it as current user
        val actualUserId = userId ?: currentUser.id
        val isCurrentUser = actualUserId == currentUser.id

        if (isCurrentUser) {
            _uiState.update {
                it.copy(
                    user = currentUser,
                    friendStatus = null,
                )
            }
            observeCurrentUserTrackings()
        } else {
            friendService.getUserProfile(actualUserId).fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            user = profile.user.user,
                            friendStatus = profile.user.friendStatus,
                            isBlocked = profile.blocked,
                            trackings = profile.trackings,
                        )
                    }
                },
                onFailure = {
                    commonUiManager.showSnackbar(Res.string.shelf_get_profile_error)
                }
            )
        }
    }

    fun observeCurrentUserTrackings() = viewModelScope.launch {
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

    fun blockUser() = viewModelScope.launch {
        val userId = _uiState.value.user?.id

        if (userId == null) {
            commonUiManager.hideSheet()
            commonUiManager.showSnackbar(Res.string.block_error)
            return@launch
        }

        _uiState.update { it.copy(isLoading = true) }

        blockService.blockUser(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isBlocked = true,
                        friendStatus = FriendStatus.STRANGER,
                    )
                }
                commonUiManager.showSnackbar(Res.string.block_success)
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.block_error)
            }
        )

        _uiState.update { it.copy(isLoading = false) }
        commonUiManager.hideSheet()
    }

    fun unblockUser() = viewModelScope.launch {
        val userId = _uiState.value.user?.id

        if (userId == null) {
            commonUiManager.hideSheet()
            commonUiManager.showSnackbar(Res.string.unblock_error)
            return@launch
        }

        _uiState.update { it.copy(isLoading = true) }

        blockService.unblockUser(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isBlocked = false,
                    )
                }
                commonUiManager.showSnackbar(Res.string.unblock_success)
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.unblock_error)
            }
        )

        _uiState.update { it.copy(isLoading = false) }
        commonUiManager.hideSheet()
    }

    fun reportUser(reason: ReportReason, message: String?) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val reportedId = _uiState.value.user?.id ?: return@launch

        val dto = ReportRequestDto(
            reportedId = reportedId,
            type = ReportType.USER,
            reason = reason,
            message = message,
        )

        reportService.report(dto).fold(
            onSuccess = {
                commonUiManager.showSnackbar(Res.string.report_success)
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.report_error)
            }
        )

        _uiState.update { it.copy(isLoading = false) }
        commonUiManager.hideSheet()
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
        _uiState.update { ShelfUiState() }
    }
}

data class ShelfUiState(
    val user: User? = null,
    val friendStatus: FriendStatus? = null,
    val isBlocked: Boolean = false,
    val selectedMediaType: MediaType = MediaType.MOVIE,
    val selectedStatus: TrackStatus? = null,
    val trackings: List<Tracking> = emptyList(),
    val isLoading: Boolean = false,
) {
    val filteredTrackings: List<Tracking>
        get() = trackings.filter {
            it.media.type == selectedMediaType && (selectedStatus == null || it.status == selectedStatus)
        }
}