package de.ashman.ontrack.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.movie.api.MovieRepository
import de.ashman.ontrack.movie.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        fetchPopular()
    }

    fun fetchPopular() {
        viewModelScope.launch {
            movieRepository.fetchPopular().collect { movies ->
                movies?.forEach {
                    _uiState.value = _uiState.value.copy(movies = movies)
                    println(it.title)
                }
            }
        }
    }
}

data class UiState(
    val movies: List<Movie> = emptyList(),
)
