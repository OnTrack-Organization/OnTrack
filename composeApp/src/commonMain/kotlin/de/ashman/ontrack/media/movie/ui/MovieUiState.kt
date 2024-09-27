package de.ashman.ontrack.media.movie.ui

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.shelf.StatusType

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val selectedMovie: Movie? = null,
    val statusCounts: Map<StatusType, Int> = emptyMap()
)