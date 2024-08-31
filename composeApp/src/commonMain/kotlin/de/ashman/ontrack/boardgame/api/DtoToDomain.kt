package de.ashman.ontrack.boardgame.api

import de.ashman.ontrack.boardgame.model.BoardGame
import de.ashman.ontrack.boardgame.model.BoardGameDto

fun BoardGameDto.toDomain(): BoardGame {
    return BoardGame(
        id = id,
        name = name,
        yearPublished = yearPublished,
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        playingTime = playingTime,
        description = description,
        age = age,
        thumbnail = thumbnail
    )
}
