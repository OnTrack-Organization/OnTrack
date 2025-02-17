package de.ashman.ontrack.features.feed.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.FriendService
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.toDomain
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

// TODO statt jedes mal zu refreshen evt doch nen flow?
class FriendsViewModel(
    private val friendService: FriendService,
    private val authService: AuthService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState
        .onStart {
            observeSearchQuery()
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
                        _uiState.update { it.copy(potentialFriends = emptyList(), resultState = FriendsResultState.Default) }
                        fetchFriendsAndRequests()
                    }

                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = search(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun fetchFriendsAndRequests() {
        viewModelScope.launch {
            val friends = friendService.getFriends().map { it.toDomain() }
            val receivedRequests = friendService.getReceivedRequests().map { it.toDomain() }
            val sentRequests = friendService.getSentRequests().map { it.toDomain() }

            _uiState.update {
                it.copy(
                    friends = friends,
                    receivedRequests = receivedRequests,
                    sentRequests = sentRequests
                )
            }
        }
    }

    fun search(query: String) = viewModelScope.launch {
        val potentialFriends = friendService.searchForNewFriends(query).map { it.toDomain() }
        _uiState.update {
            it.copy(
                potentialFriends = potentialFriends,
                resultState = if (potentialFriends.isEmpty()) FriendsResultState.Empty else FriendsResultState.Success
            )
        }
    }

    fun removeFriend(friend: Friend) {
        viewModelScope.launch {
            friendService.removeFriend(friend)
            fetchFriendsAndRequests()
        }
    }

    fun sendRequest(otherRequest: FriendRequest) {
        viewModelScope.launch {
            val myRequest = FriendRequest(
                userId = authService.currentUserId,
                username = authService.currentUserName,
                name = authService.currentUserName,
                imageUrl = authService.currentUserImage,
            )
            friendService.sendRequest(otherRequest, myRequest)
            fetchFriendsAndRequests()
        }
    }

    fun acceptRequest(friendRequest: FriendRequest) {
        viewModelScope.launch {
            friendService.acceptRequest(friendRequest)
            fetchFriendsAndRequests()
        }
    }

    fun denyRequest(friendRequest: FriendRequest) {
        viewModelScope.launch {
            friendService.denyRequest(friendRequest)
            fetchFriendsAndRequests()
        }
    }

    fun cancelRequest(friendRequest: FriendRequest) {
        viewModelScope.launch {
            friendService.cancelRequest(friendRequest)
            fetchFriendsAndRequests()
        }
    }

    fun clearViewModel() {
        _uiState.update {
            it.copy(
                query = "",
                friends = emptyList(),
                receivedRequests = emptyList(),
                sentRequests = emptyList(),
            )
        }
    }
}

data class FriendsUiState(
    val query: String = "",
    val friends: List<Friend> = emptyList(),
    val receivedRequests: List<FriendRequest> = emptyList(),
    val sentRequests: List<FriendRequest> = emptyList(),
    val potentialFriends: List<Friend> = emptyList(),
    val resultState: FriendsResultState = FriendsResultState.Default,
)

enum class FriendsResultState {
    Default,
    Empty,
    Success,
}
