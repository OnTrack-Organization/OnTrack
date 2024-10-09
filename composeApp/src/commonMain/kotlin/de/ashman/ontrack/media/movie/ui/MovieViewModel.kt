package de.ashman.ontrack.media.movie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaType
import de.ashman.ontrack.media.MediaUiState
import de.ashman.ontrack.media.movie.api.MovieRepository
import de.ashman.ontrack.media.movie.model.domain.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<Movie>())
    val uiState: StateFlow<MediaUiState<Movie>> = _uiState.asStateFlow()

    init {
        fetchMoviesByQuery("inception")
        fetchStatusCounts()
    }

    fun fetchMoviesByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { albums ->
                    _uiState.value.copy(
                        mediaList = albums,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { throwable ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            )
        }
    }

    fun fetchMovieDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { movie ->
                    _uiState.value.copy(
                        selectedMedia = movie,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { throwable ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            )
        }
    }

    fun addToList(movie: Movie) {
        viewModelScope.launch {
            userService.updateUserMedia(movie)
        }
    }

    private fun fetchStatusCounts() {
        viewModelScope.launch {
            val savedMovies = userService.getSavedMedia<Movie>(MediaType.MOVIE.name)

            val counts = savedMovies
                .mapNotNull { it.consumeStatus }
                .groupingBy { it }
                .eachCount()

            _uiState.value = _uiState.value.copy(statusCounts = counts)
        }
    }
}
