package de.ashman.ontrack.media.boardgame.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.boardgame.api.BoardGameRepository
import de.ashman.ontrack.media.model.BoardGame

class BoardGameViewModel(
    private val repository: BoardGameRepository,
    private val userService: UserService,
) : MediaViewModel<BoardGame>(repository, userService) {

    init {
        fetchMediaByQuery("catan")
    }

}