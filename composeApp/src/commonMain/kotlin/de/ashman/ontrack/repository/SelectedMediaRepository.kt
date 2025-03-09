package de.ashman.ontrack.repository

import de.ashman.ontrack.domain.media.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SelectedMediaRepository {
    val selectedMedia: StateFlow<Media?>
    fun selectMedia(media: Media?)
    fun getSelectedMedia(): Media?
    fun clearSelection()
}

class SelectedMediaRepositoryImpl : SelectedMediaRepository {

    private val _selectedMedia = MutableStateFlow<Media?>(null)
    override val selectedMedia: StateFlow<Media?> = _selectedMedia.asStateFlow()

    override fun selectMedia(media: Media?) {
        _selectedMedia.value = media
    }

    override fun getSelectedMedia(): Media? {
        return _selectedMedia.value
    }

    override fun clearSelection() {
        _selectedMedia.value = null
    }
}
