package de.ashman.ontrack.media.videogame.api

import de.ashman.ontrack.media.videogame.model.VideoGame
import de.ashman.ontrack.media.videogame.model.VideoGameDto

fun VideoGameDto.toDomain(): VideoGame =
    VideoGame(
        id = id,
        name = name
    )