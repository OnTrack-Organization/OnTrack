package de.ashman.ontrack.media.videogame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaUiState
import de.ashman.ontrack.media.videogame.api.VideoGameRepository
import de.ashman.ontrack.media.videogame.model.domain.VideoGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoGameViewModel(
    private val repository: VideoGameRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<VideoGame>())
    val uiState: StateFlow<MediaUiState<VideoGame>> = _uiState.asStateFlow()

    init {
        fetchVideogamesByQuery("smash bros")
    }

    fun fetchVideogamesByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { videogames ->
                    _uiState.value.copy(
                        mediaList = videogames,
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

    fun fetchVideogameDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { videogame ->
                    _uiState.value.copy(
                        selectedMedia = videogame,
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

    fun addToList(videoGame: VideoGame) {
        viewModelScope.launch {
            userService.updateUserMedia(videoGame)
        }
    }
}
