package de.ashman.ontrack.media.show.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.media.show.api.ShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowViewModel(
    private val showRepository: ShowRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowUiState())
    val uiState: StateFlow<ShowUiState> = _uiState.asStateFlow()

    init {
        fetchPopular()
    }

    fun fetchPopular() {
        viewModelScope.launch {
            showRepository.fetchPopular().collect { shows ->
                if (shows != null) _uiState.value = _uiState.value.copy(shows = shows)
                shows?.forEach {
                    println(it.name)
                }
            }
        }
    }
}
