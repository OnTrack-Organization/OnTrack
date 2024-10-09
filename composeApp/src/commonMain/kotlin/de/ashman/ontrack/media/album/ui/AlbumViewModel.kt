package de.ashman.ontrack.media.album.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaUiState
import de.ashman.ontrack.media.album.api.AlbumRepository
import de.ashman.ontrack.media.album.model.domain.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val repository: AlbumRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<Album>())
    val uiState: StateFlow<MediaUiState<Album>> = _uiState.asStateFlow()

    init {
        fetchAlbumsByQuery("american teen")
    }

    fun fetchAlbumsByQuery(query: String) {
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

    fun fetchAlbumDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { album ->
                    _uiState.value.copy(
                        selectedMedia = album,
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

    fun addToList(album: Album) {
        viewModelScope.launch {
            userService.updateUserMedia(album)
        }
    }
}
