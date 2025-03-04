package de.ashman.ontrack.features.feed.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.db.AuthRepository
import de.ashman.ontrack.db.FriendRepository
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.notification.NotificationService
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
import ontrack.composeapp.generated.resources.notifications_new_request_body
import ontrack.composeapp.generated.resources.notifications_new_request_title
import ontrack.composeapp.generated.resources.notifications_request_accepted_body
import ontrack.composeapp.generated.resources.notifications_request_accepted_title
import org.jetbrains.compose.resources.getString

class FriendsViewModel(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository,
    private val notificationService: NotificationService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState
        .onStart {
            observeSearchQuery()
            observeFriendsAndRequests()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    private var searchJob: Job? = null

    fun search(query: String) = viewModelScope.launch {
        val potentialFriends = friendRepository.searchForNewFriends(query)

        _uiState.update {
            it.copy(
                potentialFriends = potentialFriends,
                resultState = if (potentialFriends.isEmpty()) FriendsResultState.PotentialEmpty else FriendsResultState.Potential
            )
        }
    }

    fun removeFriend() {
        viewModelScope.launch {
            _uiState.value.selectedFriend?.let {
                friendRepository.removeFriend(it)
            }
        }
    }

    fun selectFriend(friend: Friend) {
        _uiState.update { it.copy(selectedFriend = friend) }
    }

    fun sendRequest(otherRequest: FriendRequest) = viewModelScope.launch {
        val myRequest = FriendRequest(
            userId = authRepository.currentUserId,
            username = authRepository.currentUserName,
            name = authRepository.currentUserName,
            imageUrl = authRepository.currentUserImage,
        )

        friendRepository.sendRequest(otherRequest, myRequest)

        notificationService.sendPushNotification(
            userId = otherRequest.userId,
            title = getString(Res.string.notifications_new_request_title),
            body = getString(Res.string.notifications_new_request_body, authRepository.currentUserName),
        )
    }

    fun acceptRequest(friendRequest: FriendRequest) = viewModelScope.launch {
        friendRepository.acceptRequest(friendRequest)

        notificationService.sendPushNotification(
            userId = friendRequest.userId,
            title = getString(Res.string.notifications_request_accepted_title),
            body = getString(Res.string.notifications_request_accepted_body, authRepository.currentUserName),
        )
    }

    fun declineRequest(friendRequest: FriendRequest) = viewModelScope.launch {
        friendRepository.declineRequest(friendRequest)
    }

    fun cancelRequest(friendRequest: FriendRequest) = viewModelScope.launch {
        friendRepository.cancelRequest(friendRequest)
    }

    fun clearViewModel() {
        _uiState.update { FriendsUiState() }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private fun observeFriendsAndRequests() = viewModelScope.launch {
        launch { friendRepository.getFriends().collect { friends -> _uiState.update { it.copy(friends = friends) } } }
        launch { friendRepository.getReceivedRequests().collect { receivedRequests -> _uiState.update { it.copy(receivedRequests = receivedRequests) } } }
        launch { friendRepository.getSentRequests().collect { sentRequests -> _uiState.update { it.copy(sentRequests = sentRequests) } } }
    }.invokeOnCompletion {
        updateFriendsResultState()
    }

    private fun updateFriendsResultState() {
        _uiState.update {
            it.copy(
                resultState = when {
                    it.friends.isNotEmpty() || it.receivedRequests.isNotEmpty() || it.sentRequests.isNotEmpty() -> FriendsResultState.Friends
                    else -> FriendsResultState.FriendsEmpty
                }
            )
        }
    }

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
                                potentialFriends = emptyList(),
                                resultState = if (it.friends.isNotEmpty() ||
                                    it.receivedRequests.isNotEmpty() ||
                                    it.sentRequests.isNotEmpty()
                                ) FriendsResultState.Friends else FriendsResultState.FriendsEmpty
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
}

data class FriendsUiState(
    val query: String = "",
    val friends: List<Friend> = emptyList(),
    val receivedRequests: List<FriendRequest> = emptyList(),
    val sentRequests: List<FriendRequest> = emptyList(),
    val potentialFriends: List<Friend> = emptyList(),
    val resultState: FriendsResultState = FriendsResultState.Friends,
    val selectedFriend: Friend? = null,
)

enum class FriendsResultState {
    Friends,
    FriendsEmpty,
    Potential,
    PotentialEmpty,
}
