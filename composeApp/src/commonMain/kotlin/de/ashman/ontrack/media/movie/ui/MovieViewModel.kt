package de.ashman.ontrack.media.movie.ui

import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.movie.api.MovieRepository
import de.ashman.ontrack.media.model.Movie
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository,
    private val userService: UserService,
) : MediaViewModel<Movie>(repository, userService) {

    init {
        fetchMediaByQuery("inception")
        fetchStatusCounts()
    }

    private fun fetchStatusCounts() {
        viewModelScope.launch {
            val savedMovies = userService.getSavedMedia<Movie>(MediaType.MOVIE.name)

            val counts = savedMovies
                .mapNotNull { it.consumeStatus }
                .groupingBy { it }
                .eachCount()

            updateUiState(uiState.value.copy(statusCounts = counts))
        }
    }
}
