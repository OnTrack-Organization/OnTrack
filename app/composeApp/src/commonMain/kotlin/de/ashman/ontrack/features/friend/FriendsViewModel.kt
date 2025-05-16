package de.ashman.ontrack.features.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.domain.newdomains.FriendStatus
import de.ashman.ontrack.domain.newdomains.OtherUser
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.services.friend.FriendService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.friend_accept_request_error
import ontrack.composeapp.generated.resources.friend_cancel_request_error
import ontrack.composeapp.generated.resources.friend_decline_request_error
import ontrack.composeapp.generated.resources.friend_delete_error
import ontrack.composeapp.generated.resources.friend_send_request_error

class FriendsViewModel(
    private val friendService: FriendService,
    private val commonUiManager: CommonUiManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState
        .onStart {
            observeSearchQuery()
            fetchFriendsAndRequests()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var searchJob: Job? = null

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        uiState
            .map { it.query }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _uiState.update {
                            it.copy(
                                resultState = FriendsResultState.Success,
                            )
                        }
                    }

                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = search(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchFriendsAndRequests() = viewModelScope.launch {
        _uiState.update { it.copy(resultState = FriendsResultState.Loading) }

        friendService.getFriendsAndRequests().fold(
            onSuccess = { users ->
                _uiState.update {
                    it.copy(
                        resultState = if (users.isEmpty()) FriendsResultState.QueryEmpty else FriendsResultState.Success,
                        users = users,
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(resultState = FriendsResultState.Error) }
            }
        )
    }

    fun search(query: String) = viewModelScope.launch {
        _uiState.update { it.copy(resultState = FriendsResultState.Loading) }

        friendService.getUsersByQuery(query).fold(
            onSuccess = { users ->
                _uiState.update {
                    val newUsers = users.filter { newUser ->
                        it.users.none { existingUser -> existingUser.user.id == newUser.user.id }
                    }

                    it.copy(
                        users = it.users + newUsers,
                        resultState = FriendsResultState.QuerySuccess,
                    )
                }
            },
            onFailure = {
                _uiState.update {
                    it.copy(
                        resultState = FriendsResultState.Error
                    )
                }
            }
        )
    }

    fun sendRequest(userId: String) = viewModelScope.launch {
        friendService.sendRequest(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        users = it.users.map { user ->
                            if (user.user.id == userId) user.copy(friendStatus = FriendStatus.REQUEST_SENT) else user
                        }
                    )
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_send_request_error)
            }
        )
    }

    fun acceptRequest(userId: String) = viewModelScope.launch {
        friendService.acceptRequest(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        users = it.users.map { user ->
                            if (user.user.id == userId) user.copy(friendStatus = FriendStatus.FRIEND) else user
                        }
                    )
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_accept_request_error)
            }
        )
    }

    fun declineRequest(userId: String) = viewModelScope.launch {
        friendService.declineRequest(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        users = it.users.map { user ->
                            if (user.user.id == userId) user.copy(friendStatus = FriendStatus.STRANGER) else user
                        }
                    )
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_decline_request_error)
            }
        )
    }

    fun cancelRequest(userId: String) = viewModelScope.launch {
        friendService.cancelRequest(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        users = it.users.map { user ->
                            if (user.user.id == userId) user.copy(friendStatus = FriendStatus.STRANGER) else user
                        }
                    )
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_cancel_request_error)
            }
        )
    }

    fun removeFriend(userId: String) = viewModelScope.launch {
        friendService.deleteFriend(userId).fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        users = it.users.map { user ->
                            if (user.user.id == userId) user.copy(friendStatus = FriendStatus.STRANGER) else user
                        }
                    )
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.friend_delete_error)
            }
        )
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun clearViewModel() {
        _uiState.update { FriendsUiState() }
    }
}


data class FriendsUiState(
    val query: String = "",
    val users: List<OtherUser> = emptyList(),
    val resultState: FriendsResultState = FriendsResultState.Loading,
)

enum class FriendsResultState {
    Loading,
    Error,
    Success,
    Empty,
    QuerySuccess,
    QueryEmpty,
}
