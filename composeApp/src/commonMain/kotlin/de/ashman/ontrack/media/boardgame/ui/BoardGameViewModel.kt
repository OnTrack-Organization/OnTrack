package de.ashman.ontrack.media.boardgame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.boardgame.api.BoardGameRepository
import de.ashman.ontrack.media.boardgame.model.domain.BoardGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoardGameViewModel(
    private val repository: BoardGameRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardGameUiState())
    val uiState: StateFlow<BoardGameUiState> = _uiState.asStateFlow()

    init {
        fetchBoardgamesByKeyword("catan")
    }

    fun fetchBoardgamesByKeyword(keyword: String) {
        viewModelScope.launch {
            val boardGames = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(boardGames = boardGames)
        }
    }

    fun fetchBoardgameDetails(id: String) {
        viewModelScope.launch {
            val boardgame = repository.fetchMediaDetails(id)
            _uiState.value = _uiState.value.copy(selectedBoardgame = boardgame)
        }
    }

    fun addToList(boardGame: BoardGame) {
        viewModelScope.launch {
            userService.updateUserMedia(boardGame)
        }
    }
}