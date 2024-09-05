package de.ashman.ontrack.boardgame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.boardgame.api.BoardGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoardGameViewModel(
    private val boardGameRepository: BoardGameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardGameUiState())
    val uiState: StateFlow<BoardGameUiState> = _uiState.asStateFlow()

    init {
        fetchBoardGames()
    }

    fun fetchBoardGames() {
        viewModelScope.launch {
            boardGameRepository.fetchBoardGame().collect { bg ->
                if (bg != null) _uiState.value = _uiState.value.copy(boardGames = bg)
                bg?.forEach {
                    println(it.name)
                }
            }
        }
    }
}