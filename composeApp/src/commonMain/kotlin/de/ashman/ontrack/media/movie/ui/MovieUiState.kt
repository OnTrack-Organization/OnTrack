package de.ashman.ontrack.media.movie.ui

import de.ashman.ontrack.media.movie.model.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
)