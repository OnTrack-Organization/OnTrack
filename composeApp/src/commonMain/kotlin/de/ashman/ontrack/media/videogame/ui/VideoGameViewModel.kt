package de.ashman.ontrack.media.videogame.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.videogame.api.VideoGameRepository
import de.ashman.ontrack.media.model.VideoGame

class VideoGameViewModel(
    private val repository: VideoGameRepository,
    private val userService: UserService,
) : MediaViewModel<VideoGame>(repository, userService) {

    init {
        fetchMediaByQuery("smash bros")
        fetchStatusCounts(MediaType.VIDEOGAME)
    }
}
