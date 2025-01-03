package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.di.DEFAULT_ALBUM_QUERY
import de.ashman.ontrack.media.domain.Album
import de.ashman.ontrack.media.domain.MediaType

class AlbumViewModel(
    private val repository: AlbumRepository,
    private val userService: UserService,
) : MediaViewModel<Album>(repository, userService) {

    init {
        fetchMediaByQuery(DEFAULT_ALBUM_QUERY)
        fetchStatusCounts(MediaType.ALBUM)
    }
}
