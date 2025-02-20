package de.ashman.ontrack.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.domain.user.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun signUp(user: User) = viewModelScope.launch {
        try {
            authService.signUp(user.toEntity())
        } catch (e: Exception) {
            Logger.e("Error signing up: ${e.message}")
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun signOut() = viewModelScope.launch {
        try {
            authService.signOut()
        } catch (e: Exception) {
            Logger.e("Error signing out: ${e.message}")
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun deleteUser() = viewModelScope.launch {
        try {
            authService.deleteUser()
        } catch (e: Exception) {
            Logger.e("Error deleting user", e)
            _uiState.update { it.copy(error = e.message ?: "Unknown error") }
        }
    }

    fun clearViewModel() {
        _uiState.update { AuthUiState() }
    }
}

data class AuthUiState(
    val error: String? = null,
)
