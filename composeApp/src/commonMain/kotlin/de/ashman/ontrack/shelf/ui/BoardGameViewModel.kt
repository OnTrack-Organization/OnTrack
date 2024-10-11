package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.boardgame.BoardGameRepository
import de.ashman.ontrack.media.domain.BoardGame
import de.ashman.ontrack.media.domain.MediaType

class BoardGameViewModel(
    private val repository: BoardGameRepository,
    private val userService: UserService,
) : MediaViewModel<BoardGame>(repository, userService) {

    init {
        fetchMediaByQuery("catan")
        fetchStatusCounts(MediaType.BOARDGAME)
    }

}