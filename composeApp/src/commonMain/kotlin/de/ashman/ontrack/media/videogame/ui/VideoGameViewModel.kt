package de.ashman.ontrack.media.videogame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.videogame.api.VideoGameRepository
import de.ashman.ontrack.media.videogame.api.toEntity
import de.ashman.ontrack.media.videogame.model.domain.VideoGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoGameViewModel(
    private val repository: VideoGameRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoGameUiState())
    val uiState: StateFlow<VideoGameUiState> = _uiState.asStateFlow()

    init {
        fetchGamesByKeyword("smash bros")
    }

    fun fetchGamesByKeyword(keyword: String) {
        viewModelScope.launch {
            val games = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(videogames = games)
        }
    }

    fun fetchGameDetails(id: String) {
        viewModelScope.launch {
            val game = repository.fetchMediaDetails(id)
            _uiState.value = _uiState.value.copy(selectedGame = game)
        }
    }

    fun addToList(videoGame: VideoGame) {
        viewModelScope.launch {
            userService.updateUserMedia(videoGame.toEntity())
        }
    }
}
