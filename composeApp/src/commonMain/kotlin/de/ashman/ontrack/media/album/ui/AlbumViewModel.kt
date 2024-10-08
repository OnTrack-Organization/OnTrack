package de.ashman.ontrack.media.album.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.media.album.api.AlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val repository: AlbumRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        fetchAlbumsByKeyword("american teen")
    }

    fun fetchAlbumsByKeyword(keyword: String) {
        viewModelScope.launch {
            val albums = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(albums = albums)
            println(albums)
        }
    }

    fun fetchAlbumDetails(id: String) {
        viewModelScope.launch {
            val album = repository.fetchMediaDetails(id)
            _uiState.value = _uiState.value.copy(selectedAlbum = album)
        }
    }
}
