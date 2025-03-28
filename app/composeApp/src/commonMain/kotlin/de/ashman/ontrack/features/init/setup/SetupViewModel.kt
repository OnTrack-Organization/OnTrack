package de.ashman.ontrack.features.init.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.settings.ImageUploadState
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository
import de.ashman.ontrack.storage.StorageRepository
import de.ashman.ontrack.usecase.UsernameError
import de.ashman.ontrack.usecase.UsernameValidationResult
import de.ashman.ontrack.usecase.UsernameValidationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupViewModel(
    private val firestoreUserRepository: FirestoreUserRepository,
    private val storageRepository: StorageRepository,
    private val usernameValidation: UsernameValidationUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )

    fun setUserFromLogin(user: User?) {
        _uiState.update {
            it.copy(
                user = user,
                name = user?.name.orEmpty(),
                username = user?.username.orEmpty(),
                imageUrl = user?.imageUrl,
            )
        }
    }

    suspend fun onCreateUser(): Boolean {
        val newUsername = _uiState.value.username
        val newName = _uiState.value.name
        val newImage = _uiState.value.imageUrl

        val result = usernameValidation.validate(newUsername)

        if (result is UsernameValidationResult.Invalid) {
            _uiState.update { it.copy(usernameError = result.error) }
            return false
        }

        val fcmToken = NotifierManager.getPushNotifier().getToken()

        val newUser = _uiState.value.user?.copy(
            name = newName,
            username = newUsername,
            fcmToken = fcmToken.orEmpty(),
            imageUrl = newImage.orEmpty(),
        )

        return newUser?.let {
            firestoreUserRepository.createUser(it)
            true
        } == true
    }

    fun onImagePicked(bytes: ByteArray?) = viewModelScope.launch {
        _uiState.update { it.copy(imageUploadState = ImageUploadState.Uploading) }
        bytes ?: return@launch

        val imageUrl = storageRepository.uploadUserImage(bytes)

        _uiState.update { it.copy(imageUrl = imageUrl, imageUploadState = ImageUploadState.Success) }
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
