package de.ashman.ontrack.features.init.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.newdomains.NewUser
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.settings.ImageUploadState
import de.ashman.ontrack.network.services.account.AccountResult
import de.ashman.ontrack.network.services.account.AccountService
import de.ashman.ontrack.network.services.account.UsernameError
import de.ashman.ontrack.storage.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.unknown_error

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

    fun preFillUserDataFromLogin(user: NewUser) {
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
                    is AccountResult.Success -> {
                        _uiState.update { it.copy(user = it.user?.copy(name = newName, username = newUsername)) }
                        userDataStore.saveUser(user = _uiState.value.user!!)

                        navigateToSearch()
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

    fun clearViewModel() {
        _uiState.value = SetupUiState()
    }
}

data class SetupUiState(
    // TODO maybe remove and do differently
    val user: NewUser? = null,
    val name: String = "",
    val username: String = "",
    val imageUrl: String? = null,
    val usernameError: UsernameError? = null,
    val imageUploadState: ImageUploadState = ImageUploadState.Idle,
)
