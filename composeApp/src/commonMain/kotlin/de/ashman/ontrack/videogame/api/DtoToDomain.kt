package de.ashman.ontrack.videogame.api

import de.ashman.ontrack.videogame.model.VideoGame
import de.ashman.ontrack.videogame.model.VideoGameDto

fun VideoGameDto.toDomain(): VideoGame =
    VideoGame(
        id = id,
        name = name
    )