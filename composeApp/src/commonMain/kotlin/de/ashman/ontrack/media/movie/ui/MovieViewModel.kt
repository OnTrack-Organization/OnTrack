package de.ashman.ontrack.media.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.media.movie.api.MovieRepository
import de.ashman.ontrack.media.movie.model.MovieDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
    //private val userService: UserService,
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

    fun updateMovie(movieDto: MovieDto) {
        //userService.updateUserMovie(movieDto)
    }
}
