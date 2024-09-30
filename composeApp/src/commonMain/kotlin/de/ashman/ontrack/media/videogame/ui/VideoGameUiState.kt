package de.ashman.ontrack.media.videogame.ui

import de.ashman.ontrack.media.videogame.model.domain.VideoGame

data class VideoGameUiState(
    val games: List<VideoGame> = emptyList(),
    val selectedGame: VideoGame? = null,
)
