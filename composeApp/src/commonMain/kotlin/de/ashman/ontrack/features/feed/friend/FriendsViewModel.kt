package de.ashman.ontrack.features.feed.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.firestore.FriendRepository
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
import ontrack.composeapp.generated.resources.feed_friend_removed
import ontrack.composeapp.generated.resources.feed_request_accepted
import ontrack.composeapp.generated.resources.feed_request_cancelled
import ontrack.composeapp.generated.resources.feed_request_declined
import ontrack.composeapp.generated.resources.feed_request_sent
import ontrack.composeapp.generated.resources.notifications_new_request_body
import ontrack.composeapp.generated.resources.notifications_new_request_title
import ontrack.composeapp.generated.resources.notifications_request_accepted_body
import ontrack.composeapp.generated.resources.notifications_request_accepted_title
import org.jetbrains.compose.resources.getString

class FriendsViewModel(
    private val friendRepository: FriendRepository,
    private val currentUserRepository: CurrentUserRepository,
    private val sharedUiManager: SharedUiManager,
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
    private val searchCache = mutableMapOf<String, List<Friend>>()

    init {
        viewModelScope.launch {
            currentUserRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
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

    private fun observeFriendsAndRequests() = viewModelScope.launch {
        launch { friendRepository.getFriends().collect { friends -> _uiState.update { it.copy(friends = friends) } } }
        launch { friendRepository.getReceivedRequests().collect { receivedRequests -> _uiState.update { it.copy(receivedRequests = receivedRequests) } } }
        launch { friendRepository.getSentRequests().collect { sentRequests -> _uiState.update { it.copy(sentRequests = sentRequests) } } }
    }.invokeOnCompletion {
        updateFriendsResultState()
    }

    fun search(query: String) = viewModelScope.launch {
        val normalizedQuery = query.trim().lowercase()

        val cachedResult = searchCache[normalizedQuery]
        if (cachedResult != null) {
            _uiState.update {
                it.copy(
                    potentialFriends = cachedResult,
                    resultState = if (cachedResult.isEmpty()) FriendsResultState.PotentialEmpty else FriendsResultState.Potential
                )
            }
            return@launch
        }

        val potentialFriends = friendRepository.searchForNewFriends(normalizedQuery, _uiState.value.excludedIds)

        searchCache[normalizedQuery] = potentialFriends

        _uiState.update {
            it.copy(
                potentialFriends = potentialFriends,
                resultState = if (potentialFriends.isEmpty()) FriendsResultState.PotentialEmpty else FriendsResultState.Potential
            )
        }
    }

    fun removeFriend() {
        viewModelScope.launch {
            _uiState.value.selectedFriend?.let { friendRepository.removeFriend(it) }
            sharedUiManager.showSnackbar(Res.string.feed_friend_removed)

            clearSearchCache()
        }
    }

    fun sendRequest(otherRequest: FriendRequest) = viewModelScope.launch {
        _uiState.value.user?.let { user ->
            val myRequest = FriendRequest(
                userId = user.id,
                username = user.username,
                name = user.name,
                imageUrl = user.imageUrl,
            )

            friendRepository.sendRequest(otherRequest, myRequest)
            sharedUiManager.showSnackbar(Res.string.feed_request_sent)

            notificationService.sendPushNotification(
                userId = otherRequest.userId,
                title = getString(Res.string.notifications_new_request_title),
                body = getString(Res.string.notifications_new_request_body, user.name),
            )

            clearSearchCache()
        }
    }

    fun acceptRequest(friendRequest: FriendRequest) = viewModelScope.launch {
        _uiState.value.user?.let { user ->
            friendRepository.acceptRequest(friendRequest)
            sharedUiManager.showSnackbar(Res.string.feed_request_accepted)

            notificationService.sendPushNotification(
                userId = friendRequest.userId,
                title = getString(Res.string.notifications_request_accepted_title),
                body = getString(Res.string.notifications_request_accepted_body, user.name),
            )

            clearSearchCache()
        }
    }

    fun declineRequest(friendRequest: FriendRequest) = viewModelScope.launch {
        friendRepository.declineRequest(friendRequest)
        sharedUiManager.showSnackbar(Res.string.feed_request_declined)

        clearSearchCache()
    }

    fun cancelRequest(friendRequest: FriendRequest) = viewModelScope.launch {
        friendRepository.cancelRequest(friendRequest)
        sharedUiManager.showSnackbar(Res.string.feed_request_cancelled)

        clearSearchCache()
    }

    fun selectFriend(friend: Friend) {
        _uiState.update { it.copy(selectedFriend = friend) }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
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

    private fun clearSearchCache() {
        searchCache.clear()
    }

    fun clearViewModel() {
        _uiState.update { FriendsUiState() }
        clearSearchCache()
    }
}


data class FriendsUiState(
    val user: User? = null,
    val query: String = "",
    val friends: List<Friend> = emptyList(),
    val receivedRequests: List<FriendRequest> = emptyList(),
    val sentRequests: List<FriendRequest> = emptyList(),
    val potentialFriends: List<Friend> = emptyList(),
    val resultState: FriendsResultState = FriendsResultState.Friends,
    val selectedFriend: Friend? = null,
) {
    val excludedIds: List<String>
        get() = friends.map { it.id } + receivedRequests.map { it.userId } + sentRequests.map { it.userId } + user?.id.orEmpty()
}

enum class FriendsResultState {
    Friends,
    FriendsEmpty,
    Potential,
    PotentialEmpty,
}
