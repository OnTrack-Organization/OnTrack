package de.ashman.ontrack.videogame.ui

import de.ashman.ontrack.videogame.model.VideoGame

data class VideoGameUiState(
    val games: List<VideoGame> = emptyList()
)
