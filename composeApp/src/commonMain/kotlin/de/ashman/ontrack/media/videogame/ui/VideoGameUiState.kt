package de.ashman.ontrack.media.videogame.ui

import de.ashman.ontrack.media.videogame.model.VideoGame

data class VideoGameUiState(
    val games: List<VideoGame> = emptyList()
)
