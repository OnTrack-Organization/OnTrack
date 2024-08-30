package de.ashman.ontrack.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.movie.api.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        fetchPopular()
    }

    fun fetchPopular() {
        viewModelScope.launch {
            movieRepository.fetchPopular().collect { movies ->
                if (movies != null) _uiState.value = _uiState.value.copy(movies = movies)
                movies?.forEach {
                    println(it.title)
                }
            }
        }
    }
}
