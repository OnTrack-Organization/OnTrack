package de.ashman.ontrack.media.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.movie.api.MovieRepository
import de.ashman.ontrack.media.movie.api.toEntity
import de.ashman.ontrack.media.movie.model.domain.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        fetchMoviesByKeyword("attack on titan")
    }

    fun fetchMoviesByKeyword(keyword: String) {
        viewModelScope.launch {
            val movies = movieRepository.fetchMoviesByKeyword(keyword)
            _uiState.value = _uiState.value.copy(movies = movies)
        }
    }

    fun addMovieToList(movie: Movie) {
        viewModelScope.launch {
            userService.updateUserMovie(movie.toEntity())
        }
    }

}
