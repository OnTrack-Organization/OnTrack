package de.ashman.ontrack.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.ConsumeStatus
import de.ashman.ontrack.media.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class MediaViewModel<T : Media>(
    private val repository: MediaRepository<T>,
    private val userService: UserService
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<T>())
    val uiState: StateFlow<MediaUiState<T>> = _uiState.asStateFlow()

    fun getMediaList(mediaType: MediaType) {
        viewModelScope.launch {
            try {
                val mediaList = userService.getAllSavedMediaForType<Media>(mediaType.name)
                _uiState.value = uiState.value.copy(
                    mediaList = mediaList,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun fetchMediaDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(id)

            _uiState.value = result.fold(
                onSuccess = { media ->
                    uiState.value.copy(
                        selectedMedia = media,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { throwable ->
                    uiState.value.copy(
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
            val savedMedia = userService.getAllSavedMediaForType<Media>(mediaType.name)

            val counts = savedMedia
                .mapNotNull { it.consumeStatus }
                .groupingBy { it }
                .eachCount()
                .toMutableMap()
                .apply {
                    this[ConsumeStatus.ALL] = savedMedia.size
                }

            _uiState.value = uiState.value.copy(statusCounts = counts)
        }
    }
}

data class MediaUiState<T : Media>(
    val mediaList: List<Media> = emptyList(),

    val selectedMedia: T? = null,
    val statusCounts: Map<ConsumeStatus, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)