package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.domain.MediaType
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.media.domain.Show

class ShowViewModel(
    private val repository: ShowRepository,
    private val userService: UserService
) : MediaViewModel<Show>(repository, userService) {

    init {
        fetchMediaByQuery("attack on titan")
        fetchStatusCounts(MediaType.SHOW)
    }
}
