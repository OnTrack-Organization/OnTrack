package de.ashman.ontrack.media.boardgame.api

import de.ashman.ontrack.media.boardgame.model.domain.BoardGame
import de.ashman.ontrack.media.boardgame.model.dto.BoardGameDto
import de.ashman.ontrack.media.boardgame.model.domain.Ratings
import de.ashman.ontrack.media.boardgame.model.dto.StatisticsDto

fun BoardGameDto.toDomain(): BoardGame {
    return BoardGame(
        id = id,
        name = name.value,
        coverUrl = image.orEmpty(),
        minAge = minage?.value,
        yearPublished = yearpublished?.value,
        minPlayers = minplayers?.value,
        maxPlayers = maxplayers?.value,
        playingTime = playingtime?.value,
        description = description,
        thumbnail = thumbnail,
        ratings = statistics?.ratings?.toDomain()
    )
}

fun StatisticsDto.RatingsDto.toDomain(): Ratings {
    return Ratings(
        usersRated = usersRated.value,
        average = average.value,
        numWeights = numWeights.value,
        averageWeight = averageWeight.value
    )
}