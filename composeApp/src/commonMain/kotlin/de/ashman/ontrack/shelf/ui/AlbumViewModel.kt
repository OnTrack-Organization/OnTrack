package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.media.domain.Album
import de.ashman.ontrack.media.domain.MediaType

class AlbumViewModel(
    private val repository: AlbumRepository,
    private val userService: UserService,
) : MediaViewModel<Album>(repository, userService) {

    init {
        fetchMediaByQuery("american teen")
        fetchStatusCounts(MediaType.ALBUM)
    }

    // TODO add album specific methods here
}
