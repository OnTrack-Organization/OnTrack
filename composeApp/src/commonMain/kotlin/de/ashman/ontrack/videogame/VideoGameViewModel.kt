package de.ashman.ontrack.videogame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoGameViewModel(
    private val videoGameRepository: VideoGameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoGameUiState())
    val uiState: StateFlow<VideoGameUiState> = _uiState.asStateFlow()

    init {
        fetchGames()
    }

    fun fetchGames() {
        viewModelScope.launch {
            videoGameRepository.fetchGames().collect { games ->
                if (games != null) _uiState.value = _uiState.value.copy(games = games)
                games?.forEach {
                    println("GAME " + it.name)
                }
            }
        }
    }
}
