package de.ashman.ontrack.media.show.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.movie.api.toEntity
import de.ashman.ontrack.media.show.api.ShowRepository
import de.ashman.ontrack.media.show.api.toEntity
import de.ashman.ontrack.media.show.model.domain.Show
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowViewModel(
    private val repository: ShowRepository,
    private val userService: UserService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowUiState())
    val uiState: StateFlow<ShowUiState> = _uiState.asStateFlow()

    init {
        fetchShowsByKeyword("attack on titan")
    }

    fun fetchShowsByKeyword(keyword: String) {
        viewModelScope.launch {
            val shows = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(shows = shows)
        }
    }

    fun addToList(show: Show) {
        viewModelScope.launch {
            userService.updateUserMedia(show.toEntity())
        }
    }
}
