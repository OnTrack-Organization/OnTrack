package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.newdomains.NewUser
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.FriendRequestStatus
import de.ashman.ontrack.repository.firestore.FriendRepository
import de.ashman.ontrack.repository.firestore.TrackingRepository
import de.ashman.ontrack.usecase.AcceptRequestUseCase
import de.ashman.ontrack.usecase.CancelRequestUseCase
import de.ashman.ontrack.usecase.DeclineRequestUseCase
import de.ashman.ontrack.usecase.RemoveFriendUseCase
import de.ashman.ontrack.usecase.SendRequestUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfViewModel(
    private val trackingRepository: TrackingRepository,
    private val friendRepository: FriendRepository,
    private val sendFriendRequest: SendRequestUseCase,
    private val cancelFriendRequest: CancelRequestUseCase,
    private val acceptFriendRequest: AcceptRequestUseCase,
    private val declineFriendRequest: DeclineRequestUseCase,
    private val removeFriend: RemoveFriendUseCase,
    private val userDataStore: UserDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    fun loadUser(userId: String) = viewModelScope.launch {
        // TODO make a call to a backend method instead later
        val user = userDataStore.getCurrentUser()
        _uiState.update {
            it.copy(
                user = user,
                isCurrentUser = user.id == Firebase.auth.currentUser?.uid
            )
        }
        observeUserTrackings(userId)
    }

    fun observeUserTrackings(userId: String) {
        trackingRepository.observeTrackings(userId)
            .onEach { trackings -> _uiState.update { it.copy(trackings = trackings) } }
            .launchIn(viewModelScope)
    }

    fun sendRequest() = viewModelScope.launch {
        _uiState.value.user?.let { user ->
            sendFriendRequest(user.toFriendRequest())
            _uiState.update { it.copy(friendRequestStatus = FriendRequestStatus.PENDING) }
        }
    }

    fun cancelRequest() = viewModelScope.launch {
        _uiState.value.user?.let { user ->
            cancelFriendRequest(user.toFriendRequest())
            _uiState.update { it.copy(friendRequestStatus = FriendRequestStatus.NONE) }
        }
    }

    fun removeFriend() = viewModelScope.launch {
        _uiState.value.user?.let {
            val friend = it.toFriend()
            removeFriend(friend)
            _uiState.update { it.copy(friendRequestStatus = FriendRequestStatus.NONE) }
        }
    }

    fun declineRequest() = viewModelScope.launch {
        _uiState.value.user?.let {
            val friendRequest = it.toFriendRequest()
            declineFriendRequest(friendRequest)
            _uiState.update { it.copy(friendRequestStatus = FriendRequestStatus.NONE) }
        }
    }

    fun acceptRequest() = viewModelScope.launch {
        _uiState.value.user?.let {
            val friendRequest = it.toFriendRequest()
            acceptFriendRequest(friendRequest)
            _uiState.update { it.copy(friendRequestStatus = FriendRequestStatus.ACCEPTED) }
        }
    }

    fun setFriendRequestStatus(userId: String) = viewModelScope.launch {
        val status = friendRepository.getFriendStatus(userId)
        _uiState.update { it.copy(friendRequestStatus = status) }
    }

    fun isOtherUser() = uiState.value.user?.id != Firebase.auth.currentUser?.uid

    fun clearViewModel() {
        _uiState.update { ShelfUiState() }
    }
}

data class ShelfUiState(
    val user: NewUser? = null,
    val isCurrentUser: Boolean = false,
    val friendRequestStatus: FriendRequestStatus = FriendRequestStatus.NONE,
    val trackings: List<Tracking> = emptyList(),
)