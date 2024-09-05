package de.ashman.ontrack.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(
    private val musicRepository: MusicRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        fetchArtists()
    }

    fun fetchArtists() {
        viewModelScope.launch {
            musicRepository.fetchArtist().collect { artists ->
                if (artists != null) _uiState.value = _uiState.value.copy(artists = artists)
                artists?.forEach {
                    println("ARTIST " + it.name)
                }
            }
        }
    }
}
