package de.ashman.ontrack.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO probably use this instead of thousand viewmodels
abstract class MediaViewModel<T>(
    private val repository: MediaRepository<T>,
    private val userService: UserService
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<T>())
    val uiState: StateFlow<MediaUiState<T>> = _uiState.asStateFlow()

    fun fetchMediaByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { mediaList ->
                    _uiState.value.copy(
                        mediaList = mediaList,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { throwable ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            )
        }
    }
}
