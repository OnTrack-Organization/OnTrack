package de.ashman.ontrack.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.database.TrackingRepository
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.services.account.AccountResult
import de.ashman.ontrack.network.services.account.AccountService
import de.ashman.ontrack.network.services.account.UsernameError
import de.ashman.ontrack.network.services.signin.SignInService
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
import ontrack.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val storageRepository: StorageRepository,
    private val commonUiManager: CommonUiManager,
    private val signInService: SignInService,
    private val accountService: AccountService,
    private val userDataStore: UserDataStore,
    private val trackingRepository: TrackingRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    init {
        viewModelScope.launch {
            userDataStore.currentUser.collect { user ->
                user ?: return@collect
                _uiState.update {
                    it.copy(
                        user = user,
                        name = user.name,
                        username = user.username,
                        email = user.email,
                        imageUrl = user.profilePictureUrl,
                    )
                }
            }
        }
    }

    fun onSave() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val newUsername = _uiState.value.username
        val newName = _uiState.value.name

        accountService.updateAccountSettings(username = newUsername, name = newName).fold(
            onSuccess = { result ->
                when (result) {
                    is AccountResult.Success -> {
                        _uiState.update { it.copy(user = it.user?.copy(name = newName, username = newUsername)) }
                        userDataStore.saveUser(user = _uiState.value.user!!)

                        commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.settings_account_data_saved))
                    }

                    is AccountResult.InvalidUsername -> {
                        _uiState.update {
                            it.copy(usernameError = result.error)
                        }
                    }
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.unknown_error)
            }
        )

        _uiState.update { it.copy(isLoading = false) }
    }

    fun signOut(clearAndNavigateToStart: () -> Unit) = viewModelScope.launch {
        signInService.signOut().fold(
            onSuccess = {
                userDataStore.clearUser()
                trackingRepository.deleteAllTrackings()

                clearAndNavigateToStart()
            },
            onFailure = {
                commonUiManager.hideSheetAndShowSnackbar(getString(Res.string.logout_offline_error))
            }
        )
    }

    // TODO add later again
    fun removeUser() = viewModelScope.launch {
        commonUiManager.hideSheet()
    }

    fun onImagePicked(bytes: ByteArray?) = viewModelScope.launch {
        _uiState.update { it.copy(imageUploadState = ImageUploadState.Uploading) }

        bytes ?: return@launch

        val profilePictureUrl = storageRepository.uploadUserImage(bytes = bytes, fileName = _uiState.value.user!!.id)

        accountService.updateProfilePicture(profilePictureUrl)

        _uiState.update { it.copy(imageUrl = profilePictureUrl, imageUploadState = ImageUploadState.Success) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    fun clearUnsavedChanges() {
        _uiState.update { it.copy(name = it.user?.name.orEmpty(), username = it.user?.username.orEmpty()) }
    }

    fun clearViewModel() {
        _uiState.value = SettingsUiState()
    }
}

data class SettingsUiState(
    val user: User? = null,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val usernameError: UsernameError? = null,
    val imageUploadState: ImageUploadState = ImageUploadState.Idle,
    val isLoading: Boolean = false,
)

enum class ImageUploadState {
    Idle,
    Uploading,
    Success
}