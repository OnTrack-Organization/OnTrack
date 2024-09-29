package de.ashman.ontrack.media.show.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.media.show.api.ShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowViewModel(
    private val repository: ShowRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowUiState())
    val uiState: StateFlow<ShowUiState> = _uiState.asStateFlow()

    init {
        fetchShowsByKeyword("attack on titan")
    }

    fun fetchShowsByKeyword(keyword: String) {
        viewModelScope.launch {
            val shows = repository.fetchMediaByKeyword(keyword)
            _uiState.value = _uiState.value.copy(shows = shows)
        }
    }
}
