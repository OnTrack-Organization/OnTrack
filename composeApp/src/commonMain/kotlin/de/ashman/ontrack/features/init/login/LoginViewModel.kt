package de.ashman.ontrack.features.init.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.domain.user.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.login_error
import org.jetbrains.compose.resources.getString

class LoginViewModel(
    private val authService: AuthService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun signIn(result: Result<User?>, onSuccess: () -> Unit) = viewModelScope.launch {
        result.fold(
            onSuccess = { user ->
                if (user != null) {
                    authService.createUser(user.toEntity())
                    onSuccess()
                }
            },
            onFailure = { error ->
                Logger.e("Login failed: ${error.message}")
                _uiState.update { it.copy(snackbarMessage = getString(Res.string.login_error)) }
            }
        )
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearViewModel() {
        _uiState.update { LoginUiState() }
    }
}

data class LoginUiState(
    val snackbarMessage: String? = null,
)
