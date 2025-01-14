package de.ashman.ontrack.user.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.user.UserService
import de.ashman.ontrack.user.model.toDomain
import de.ashman.ontrack.user.model.toDto
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun registerUser(user: FirebaseUser) {
        val userDto = user.toDto()

        _uiState.value = _uiState.value.copy(
            user = userDto.toDomain()
        )

        viewModelScope.launch {
            userService.saveUser(userDto)
        }
    }

    fun getUser() {
        val currentUser = Firebase.auth.currentUser

        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(
                user = currentUser.toDto().toDomain()
            )
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            Firebase.auth.signOut()
        }

        _uiState.value = _uiState.value.copy(
            user = null
        )
    }
}

