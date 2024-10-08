package de.ashman.ontrack.media.album.ui

import de.ashman.ontrack.media.album.model.domain.Album

data class AlbumUiState(
    val albums: List<Album> = emptyList(),
    val selectedAlbum: Album? = null,
)
