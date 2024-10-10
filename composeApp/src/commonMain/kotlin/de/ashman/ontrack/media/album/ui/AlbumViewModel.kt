package de.ashman.ontrack.media.album.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.album.api.AlbumRepository
import de.ashman.ontrack.media.model.Album

class AlbumViewModel(
    private val repository: AlbumRepository,
    private val userService: UserService,
) : MediaViewModel<Album>(repository, userService) {

    init {
        fetchMediaByQuery("american teen")
    }

    // TODO add album specific methods here
}
