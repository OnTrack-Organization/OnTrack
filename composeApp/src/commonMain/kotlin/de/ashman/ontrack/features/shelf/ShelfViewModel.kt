package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.authentication.user.User
import de.ashman.ontrack.authentication.user.toDomain
import de.ashman.ontrack.db.FirestoreService
import de.ashman.ontrack.db.toDomain
import de.ashman.ontrack.domain.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShelfViewModel(
    private val firestoreService: FirestoreService,
    private val authService: AuthService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    var listState: LazyListState by mutableStateOf(LazyListState(0, 0))

    fun observeUser(userId: String) {
        viewModelScope.launch {
            authService.getUserFlow(userId)
                .collect { user ->
                    _uiState.update { it.copy(user = user?.toDomain()) }
                }
        }
    }

    fun observeUserMedia(userId: String) {
        viewModelScope.launch {
            firestoreService.consumeLatestUserTrackings(userId = userId)
                .collect { trackings ->
                    val mediaList = trackings.mapNotNull {
                        firestoreService.getMediaById(it.mediaId)?.toDomain()
                    }
                    _uiState.update { it.copy(mediaList = mediaList) }
                }
        }
    }
}

data class ShelfUiState(
    val user: User? = null,
    val mediaList: List<Media> = emptyList(),
)