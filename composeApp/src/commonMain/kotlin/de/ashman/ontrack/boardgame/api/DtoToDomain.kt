package de.ashman.ontrack.boardgame.api

import de.ashman.ontrack.boardgame.model.BoardGame
import de.ashman.ontrack.boardgame.model.BoardGameDto

fun BoardGameDto.toDomain(): BoardGame {
    return BoardGame(
        id = id,
        name = name.value,
        minAge = minAge?.value,
        yearPublished = yearPublished?.value,
        minPlayers = minPlayers?.value,
        maxPlayers = maxPlayers?.value,
        playingTime = playingTime?.value,
        description = description,
        thumbnail = thumbnail,
        image = image,
    )
}
