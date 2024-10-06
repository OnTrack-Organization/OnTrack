package de.ashman.ontrack.media.boardgame.ui

import de.ashman.ontrack.media.boardgame.model.domain.BoardGame

data class BoardGameUiState(
    val boardGames: List<BoardGame> = emptyList(),
    val selectedBoardgame: BoardGame? = null,
)