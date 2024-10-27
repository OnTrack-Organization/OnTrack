package de.ashman.ontrack.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.domain.Media
import de.ashman.ontrack.media.domain.ConsumeStatus
import de.ashman.ontrack.media.domain.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO probably use this instead of thousand viewmodels
abstract class MediaViewModel<T : Media>(
    private val repository: MediaRepository<T>,
    private val userService: UserService
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<T>())
    val uiState: StateFlow<MediaUiState<T>> = _uiState.asStateFlow()

    // TODO remove from list
    // TODO update consume status

    fun updateUiState(newState: MediaUiState<T>) {
        _uiState.value = newState
    }

    fun fetchMediaByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { mediaList ->
                    _uiState.value.copy(
                        mediaList = mediaList,
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

    fun fetchMediaDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { media ->
                    _uiState.value.copy(
                        selectedMedia = media,
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

    fun updateSelectedUi(media: T?) {
        _uiState.value = _uiState.value.copy(selectedMedia = media)
    }

    fun updateSelectedDb(media: Media?) {
        viewModelScope.launch {
            if (media != null) userService.updateUserMedia(media)
        }
    }

    fun fetchStatusCounts(mediaType: MediaType) {
        viewModelScope.launch {
            val savedMedia = userService.getSavedMedia<Media>(mediaType.name)

            val counts = savedMedia
                .mapNotNull { it.consumeStatus }
                .groupingBy { it }
                .eachCount()
                .toMutableMap()
                .apply {
                    this[ConsumeStatus.ALL] = savedMedia.size
                }

            updateUiState(uiState.value.copy(statusCounts = counts))
        }
    }
}
