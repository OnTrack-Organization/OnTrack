package de.ashman.ontrack.media.movie.ui

import de.ashman.ontrack.media.movie.model.domain.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val selectedMovie: Movie? = null,
)