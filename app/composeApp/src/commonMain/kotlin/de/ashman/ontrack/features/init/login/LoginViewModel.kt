package de.ashman.ontrack.features.init.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mmk.kmpnotifier.notification.NotifierManager
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.user.NewUser
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.signin.SignInResult
import de.ashman.ontrack.network.signin.SignInService
import dev.gitlive.firebase.auth.FirebaseUser
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
    private val signInService: SignInService,
    private val commonUiManager: CommonUiManager,
    private val userDataStore: UserDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun signIn(
        loginResult: Result<FirebaseUser?>,
        onNavigateToSearch: () -> Unit,
        onNavigateToSetup: (NewUser) -> Unit,
    ) = viewModelScope.launch {
        loginResult.fold(
            onSuccess = { user ->
                val fcmToken = NotifierManager.getPushNotifier().getToken().orEmpty()

                signInService.signIn(fcmToken).fold(
                    onSuccess = { signInResult ->
                        when (signInResult) {
                            is SignInResult.ExistingUser -> {
                                userDataStore.saveUser(signInResult.user)
                                onNavigateToSearch()
                            }

                            is SignInResult.NewUserCreated -> onNavigateToSetup(signInResult.user)
                        }
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
}

data class LoginUiState(
    val errorMessage: String? = null,
)
