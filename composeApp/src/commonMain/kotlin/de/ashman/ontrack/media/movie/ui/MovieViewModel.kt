package de.ashman.ontrack.media.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.movie.api.MovieRepository
import de.ashman.ontrack.media.movie.api.toEntity
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

    fun fetchMovieDetails(id: Int) {
        viewModelScope.launch {
            val movie = movieRepository.fetchMovieDetails(id)
            _uiState.value = _uiState.value.copy(selectedMovie = movie)
        }
    }

    fun addSelectedMovieToList() {
        viewModelScope.launch {
            val movie = uiState.value.selectedMovie
            if (movie != null) userService.updateUserMovie(movie.toEntity())
        }
    }

}
