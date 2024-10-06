package de.ashman.ontrack.media.videogame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.media.videogame.api.VideoGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoGameViewModel(
    private val repository: VideoGameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoGameUiState())
    val uiState: StateFlow<VideoGameUiState> = _uiState.asStateFlow()

    init {
        fetchGamesByKeyword("attack on titan")
    }

    fun fetchGamesByKeyword(keyword: String) {
        viewModelScope.launch {
            val games = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(games = games)
        }
    }

    fun fetchGameDetails(id: String) {
        viewModelScope.launch {
            val game = repository.fetchMediaDetails(id)
            _uiState.value = _uiState.value.copy(selectedGame = game)
        }
    }
}
