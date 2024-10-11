package de.ashman.ontrack.media.show.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.show.api.ShowRepository
import de.ashman.ontrack.media.model.Show

class ShowViewModel(
    private val repository: ShowRepository,
    private val userService: UserService
) : MediaViewModel<Show>(repository, userService) {

    init {
        fetchMediaByQuery("attack on titan")
        fetchStatusCounts(MediaType.SHOW)
    }
}
