package de.ashman.ontrack.media.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.movie.api.MovieRepository
import de.ashman.ontrack.media.movie.api.toEntity
import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.entity.MovieEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        fetchMoviesByKeyword("inception")
        fetchStatusCounts()
    }

    fun fetchMoviesByKeyword(keyword: String) {
        viewModelScope.launch {
            val movies = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(movies = movies)
        }
    }

    fun fetchMovieDetails(id: String) {
        viewModelScope.launch {
            val movie = repository.fetchMediaDetails(id)
            _uiState.value = _uiState.value.copy(selectedMovie = movie)
        }
    }

    fun addToList(movie: Movie) {
        viewModelScope.launch {
            userService.updateUserMedia(movie.toEntity())
        }
    }

    private fun fetchStatusCounts() {
        viewModelScope.launch {
            val savedMovies = userService.getSavedMedia<MovieEntity>("movie")

            val counts = savedMovies
                .groupingBy { it.watchStatus }
                .eachCount()

            _uiState.value = _uiState.value.copy(statusCounts = counts)
        }
    }
}
