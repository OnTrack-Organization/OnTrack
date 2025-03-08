package de.ashman.ontrack.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.db.AuthRepository
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.storage.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.logout_offline_error
import ontrack.composeapp.generated.resources.settings_account_data_saved
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun observeUser(userId: String) = viewModelScope.launch {
        authRepository.observeUser(userId)
            .collect { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        name = user?.name.orEmpty(),
                        username = user?.username.orEmpty(),
                        imageUrl = user?.imageUrl,
                    )
                }
            }
    }

    fun deleteAccount() = viewModelScope.launch {
        authRepository.removeUser()
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

            if (authRepository.isUsernameTaken(newUsername)) {
                _uiState.update { it.copy(usernameError = UsernameError.TAKEN) }
                return@launch
            }
        }

        authRepository.updateUser(
            currentUser.copy(
                name = newName,
                username = newUsername
            )
        )

        _uiState.update { it.copy(snackbarMessage = getString(Res.string.settings_account_data_saved)) }
    }

    fun signOut(onSuccess: () -> Unit) = viewModelScope.launch {
        try {
            authRepository.signOut()
            onSuccess()
        } catch (e: Exception) {
            Logger.e("Error signing out: ${e.message}")

            _uiState.update { it.copy(snackbarMessage = getString(Res.string.logout_offline_error)) }
        }
    }

    fun onImagePicked(bytes: ByteArray?) = viewModelScope.launch {
        val currentUser = _uiState.value.user ?: return@launch
        bytes ?: return@launch

        val imageUrl = storageRepository.uploadUserImage(bytes)

        authRepository.updateUser(
            currentUser.copy(imageUrl = imageUrl)
        )

        _uiState.update { it.copy(imageUrl = imageUrl) }
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
    val imageUrl: String? = null,
    val usernameError: UsernameError? = null,
    val snackbarMessage: String? = null,
)

enum class UsernameError {
    EMPTY,
    TAKEN,
    TOO_LONG,
    TOO_SHORT,
}