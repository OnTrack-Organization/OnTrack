package de.ashman.ontrack.features.shelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.authentication.user.User
import de.ashman.ontrack.authentication.user.toDomain
import de.ashman.ontrack.db.MediaService
import de.ashman.ontrack.db.entity.toDomain
import de.ashman.ontrack.domain.Media
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfViewModel(
    private val mediaService: MediaService,
    private val authService: AuthService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState
        .onStart {
            observeUser()
        }.onEach {
            observeMediaList()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    // TODO handle other users, not just logged in one
    private fun observeUser() {
        val userId = Firebase.auth.currentUser?.uid.orEmpty()

        viewModelScope.launch {
            authService.getUserFlow(userId)
                .collect { user ->
                    _uiState.update { it.copy(user = user?.toDomain()) }
                }
        }
    }

    private fun observeMediaList() {
        viewModelScope.launch {
            mediaService.getUserMediaListFlow().collect { mediaList ->
                _uiState.update { it.copy(mediaList = mediaList.map { it.toDomain() }) }
            }
        }
    }
}

data class ShelfUiState(
    val user: User? = null,
    val mediaList: List<Media> = emptyList(),
)