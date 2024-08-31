package de.ashman.ontrack.videogame

fun VideoGameDto.toDomain(): VideoGame =
    VideoGame(
        id = id,
        name = name
    )