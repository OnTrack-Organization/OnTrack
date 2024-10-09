package de.ashman.ontrack.media.show.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaUiState
import de.ashman.ontrack.media.album.model.domain.Album
import de.ashman.ontrack.media.show.api.ShowRepository
import de.ashman.ontrack.media.show.model.domain.Show
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowViewModel(
    private val repository: ShowRepository,
    private val userService: UserService
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<Show>())
    val uiState: StateFlow<MediaUiState<Show>> = _uiState.asStateFlow()

    init {
        fetchShowsByQuery("attack on titan")
    }

    fun fetchShowsByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { shows ->
                    _uiState.value.copy(
                        mediaList = shows,
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

    fun fetchShowDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { show ->
                    _uiState.value.copy(
                        selectedMedia = show,
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

    fun addToList(show: Show) {
        viewModelScope.launch {
            userService.updateUserMedia(show)
        }
    }
}
