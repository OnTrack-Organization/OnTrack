package de.ashman.ontrack.boardgame.ui

import de.ashman.ontrack.boardgame.model.BoardGame

data class BoardGameUiState(
    val boardGames: List<BoardGame> = emptyList(),
)