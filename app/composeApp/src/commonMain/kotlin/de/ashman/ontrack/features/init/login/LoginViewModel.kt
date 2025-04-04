package de.ashman.ontrack.features.init.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mmk.kmpnotifier.notification.NotifierManager
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.UserService
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.login_backend_error
import ontrack.composeapp.generated.resources.login_offline_error

class LoginViewModel(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val currentUserRepository: CurrentUserRepository,
    private val commonUiManager: CommonUiManager,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun signIn(
        loginResult: Result<User?>,
        onSuccessNavigate: (User?) -> Unit
    ) = viewModelScope.launch {
        loginResult.fold(
            onSuccess = { user ->
                userService.signIn().fold(
                    onSuccess = {
                        // Set fcm token on login
                        NotifierManager.getPushNotifier().getToken()?.let { fcmToken ->
                            firestoreUserRepository.updateFcmToken(fcmToken)
                        }

                        // Set current user if user does not exist
                        if (!firestoreUserRepository.doesUserExist(user?.id!!)) {
                            currentUserRepository.setCurrentUser(user)
                        }

                        onSuccessNavigate(user)

                        Logger.i("Backend sign in successful: ${user.id}")
                    },
                    onFailure = { error ->
                        Logger.e("Backend sign in failed: ${error.message}")
                        commonUiManager.showSnackbar(Res.string.login_backend_error)
                    }
                )
            },
            onFailure = { error ->
                Logger.e("Google/Apple Login failed: ${error.message}")

                if (error.message == "Idtoken is null") return@launch
                commonUiManager.showSnackbar(Res.string.login_offline_error)
            }
        )
    }

    fun clearViewModel() {
        _uiState.update { LoginUiState() }
    }

    suspend fun doesUserExist(userId: String?): Boolean {
        if (userId == null) return false
        return try {
            firestoreUserRepository.doesUserExist(userId)
        } catch (e: Exception) {
            Logger.e("Error checking if user exists: ${e.message}")
            false
        }
    }
}

data class LoginUiState(
    val errorMessage: String? = null,
)
