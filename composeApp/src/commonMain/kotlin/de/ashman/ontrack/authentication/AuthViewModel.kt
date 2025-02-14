package de.ashman.ontrack.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.domain.user.toDomain
import de.ashman.ontrack.domain.user.toEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
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

    init {
        // Login with Firebase after closing the app
        val currentUser = Firebase.auth.currentUser?.toEntity()?.toDomain()
        _uiState.update { it.copy(user = currentUser) }
        Logger.i { "Initialized AuthViewModel" }
    }

    fun signUp(user: FirebaseUser) = viewModelScope.launch {
        try {
            val userEntity = user.toEntity()

            authService.signUpUser(userEntity)

            _uiState.update { it.copy(user = userEntity.toDomain()) }
        } catch (e: Exception) {
            Logger.e("Error signing up: ${e.message}")
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun logout() = viewModelScope.launch {
        try {
            Firebase.auth.signOut()
            _uiState.update { it.copy(user = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun deleteAccount() = viewModelScope.launch {
        val userId = _uiState.value.user?.id
        if (userId == null) {
            _uiState.update { it.copy(error = "No user to delete") }
            return@launch
        }

        try {
            authService.deleteUser(userId)
            _uiState.update { it.copy(user = null, error = null) }
        } catch (e: Exception) {
            Logger.e("Error deleting account", e)
            _uiState.update { it.copy(error = e.message ?: "Unknown error") }
        }
    }
}

data class AuthUiState(
    val user: User? = null,
    val error: String? = null,
)
