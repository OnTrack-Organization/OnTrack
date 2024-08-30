package de.ashman.ontrack.movie.ui

import de.ashman.ontrack.movie.model.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
)