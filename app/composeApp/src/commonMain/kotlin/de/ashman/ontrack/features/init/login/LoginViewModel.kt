package de.ashman.ontrack.features.init.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mmk.kmpnotifier.notification.NotifierManager
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.SharedUiManager
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
import ontrack.composeapp.generated.resources.login_offline_error

class LoginViewModel(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val currentUserRepository: CurrentUserRepository,
    private val sharedUiManager: SharedUiManager,
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
        onSuccess: (User?) -> Unit
    ) = viewModelScope.launch {
        loginResult.fold(
            onSuccess = { user ->
                onSuccess(user)

                val fcmToken = NotifierManager.getPushNotifier().getToken()
                fcmToken?.let {
                    firestoreUserRepository.updateFcmToken(fcmToken)
                }

                if (!firestoreUserRepository.doesUserExist(user?.id!!)) {
                    currentUserRepository.setCurrentUser(user)
                }
            },
            onFailure = { error ->
                Logger.e("Login failed: ${error.message}")

                if (error.message == "Idtoken is null") return@launch
                sharedUiManager.showSnackbar(Res.string.login_offline_error)
            }
        )
    }

    suspend fun authOnBackend(idToken: String) = safeApiCall {
        userService.signIn(idToken)
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
