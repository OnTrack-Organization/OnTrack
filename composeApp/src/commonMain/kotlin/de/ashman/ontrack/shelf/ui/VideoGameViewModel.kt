package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.videogame.VideoGameRepository
import de.ashman.ontrack.media.domain.VideoGame
import de.ashman.ontrack.media.domain.MediaType

class VideoGameViewModel(
    private val repository: VideoGameRepository,
    private val userService: UserService,
) : MediaViewModel<VideoGame>(repository, userService) {

    init {
        fetchMediaByQuery("smash bros")
        fetchStatusCounts(MediaType.VIDEOGAME)
    }
}
