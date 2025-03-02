package de.ashman.ontrack.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.entity.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.logout_error
import ontrack.composeapp.generated.resources.settings_account_data_saved
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val authService: AuthService
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun observeUser(userId: String) = viewModelScope.launch {
        authService.observeUser(userId)
            .collect { user ->
                _uiState.update {
                    it.copy(
                        user = user?.toDomain(),
                        name = user?.displayName.orEmpty(),
                        username = user?.username.orEmpty()
                    )
                }
            }
    }

    fun deleteAccount() = viewModelScope.launch {
        authService.removeUser()
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    fun onUpdateUser() = viewModelScope.launch {
        val currentUser = _uiState.value.user ?: return@launch
        val newUsername = _uiState.value.username
        val newName = _uiState.value.name

        // Validate username only if it has changed
        if (currentUser.username != newUsername) {
            if (newUsername.isBlank()) {
                _uiState.update { it.copy(usernameError = UsernameError.EMPTY) }
                return@launch
            }

            if (newUsername.length < 5) {
                _uiState.update { it.copy(usernameError = UsernameError.TOO_SHORT) }
                return@launch
            }

            if (newUsername.length > 25) {
                _uiState.update { it.copy(usernameError = UsernameError.TOO_LONG) }
                return@launch
            }

            if (authService.isUsernameTaken(newUsername)) {
                _uiState.update { it.copy(usernameError = UsernameError.TAKEN) }
                return@launch
            }
        }

        authService.updateUser(
            currentUser.copy(
                name = newName,
                username = newUsername
            ).toEntity()
        )

        _uiState.update { it.copy(snackbarMessage = getString(Res.string.settings_account_data_saved)) }
    }

    fun signOut(onSuccess: () -> Unit) = viewModelScope.launch {
        try {
            authService.signOut()
            onSuccess()
        } catch (e: Exception) {
            Logger.e("Error signing out: ${e.message}")

            _uiState.update { it.copy(snackbarMessage = getString(Res.string.logout_error)) }
        }
    }

    fun clearUnsavedChanges() {
        _uiState.update { it.copy(name = it.user?.name.orEmpty(), username = it.user?.username.orEmpty()) }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearViewModel() {
        _uiState.value = SettingsUiState()
    }
}

data class SettingsUiState(
    val user: User? = null,
    val name: String = "",
    val username: String = "",
    val usernameError: UsernameError? = null,
    val snackbarMessage: String? = null,
)

enum class UsernameError {
    EMPTY,
    TAKEN,
    TOO_LONG,
    TOO_SHORT,
}