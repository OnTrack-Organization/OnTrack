package de.ashman.ontrack.features.init.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.settings.ImageUploadState
import de.ashman.ontrack.network.services.account.AccountService
import de.ashman.ontrack.network.services.account.AccountSettingsResult
import de.ashman.ontrack.network.services.account.UsernameError
import de.ashman.ontrack.storage.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.settings_account_data_save_error
import ontrack.composeapp.generated.resources.settings_update_picture_error
import ontrack.composeapp.generated.resources.settings_update_picture_success

class SetupViewModel(
    private val storageRepository: StorageRepository,
    private val accountService: AccountService,
    private val commonUiManager: CommonUiManager,
    private val userDataStore: UserDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun preFillUserDataFromLogin(user: User) {
        _uiState.update {
            it.copy(
                user = user,
                name = user.name,
                username = user.username,
                imageUrl = user.profilePictureUrl,
            )
        }
    }

    fun onSave(navigateToSearch: () -> Unit) = viewModelScope.launch {
        val newName = _uiState.value.name
        val newUsername = _uiState.value.username

        accountService.updateAccountSettings(username = newUsername, name = newName).fold(
            onSuccess = { result ->
                when (result) {
                    is AccountSettingsResult.Success -> {
                        _uiState.update { it.copy(usernameError = null) }
                        userDataStore.saveUser(result.user)
                        navigateToSearch()
                    }

                    is AccountSettingsResult.InvalidUsername -> {
                        _uiState.update { it.copy(usernameError = result.error) }
                    }
                }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.settings_account_data_save_error)
            }
        )
    }

    fun onImagePicked(bytes: ByteArray?) = viewModelScope.launch {
        if (bytes == null) {
            _uiState.update { it.copy(imageUploadState = ImageUploadState.Idle) }
            return@launch
        }

        _uiState.update { it.copy(imageUploadState = ImageUploadState.Uploading) }

        val profilePictureUrl = storageRepository.uploadUserImage(
            bytes = bytes,
            fileName = _uiState.value.user!!.id
        )

        accountService.updateProfilePicture(profilePictureUrl).fold(
            onSuccess = { updatedUser ->
                _uiState.update { it.copy(imageUploadState = ImageUploadState.Success) }
                userDataStore.saveUser(updatedUser)
                commonUiManager.showSnackbar(Res.string.settings_update_picture_success)
            },
            onFailure = {
                _uiState.update { it.copy(imageUploadState = ImageUploadState.Idle) }
                commonUiManager.showSnackbar(Res.string.settings_update_picture_error)
            }
        )
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    fun clearViewModel() {
        _uiState.value = SetupUiState()
    }
}

data class SetupUiState(
    val user: User? = null,
    val name: String = "",
    val username: String = "",
    val imageUrl: String? = null,
    val usernameError: UsernameError? = null,
    val imageUploadState: ImageUploadState = ImageUploadState.Idle,
)
