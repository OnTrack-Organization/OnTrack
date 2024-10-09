package de.ashman.ontrack.media.boardgame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaUiState
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
    private val _uiState = MutableStateFlow(MediaUiState<BoardGame>())
    val uiState: StateFlow<MediaUiState<BoardGame>> = _uiState.asStateFlow()

    init {
        fetchBoardgamesByQuery("catan")
    }

    fun fetchBoardgamesByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { boardgames ->
                    _uiState.value.copy(
                        mediaList = boardgames,
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

    fun fetchBoardgameDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { boardgame ->
                    _uiState.value.copy(
                        selectedMedia = boardgame,
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

    fun addToList(boardGame: BoardGame) {
        viewModelScope.launch {
            userService.updateUserMedia(boardGame)
        }
    }
}