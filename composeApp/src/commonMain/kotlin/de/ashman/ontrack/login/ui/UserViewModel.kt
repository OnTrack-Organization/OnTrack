package de.ashman.ontrack.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.login.model.toDomain
import de.ashman.ontrack.login.model.toDto
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun saveUser(user: FirebaseUser) {
        val userDto = user.toDto()

        _uiState.value = _uiState.value.copy(
            user = userDto.toDomain()
        )

        viewModelScope.launch {
            userService.saveUser(userDto)
        }
    }
}

