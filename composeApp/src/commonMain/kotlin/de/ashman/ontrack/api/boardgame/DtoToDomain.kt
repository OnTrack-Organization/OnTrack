package de.ashman.ontrack.api.boardgame

import de.ashman.ontrack.media.domain.BoardGame
import de.ashman.ontrack.api.boardgame.dto.BoardGameDto
import de.ashman.ontrack.media.domain.Ratings
import de.ashman.ontrack.api.boardgame.dto.StatisticsDto

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
        description = description?.decodeHtmlManually(),
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

fun String.decodeHtmlManually(): String {
    return this
        .replace("&amp;#10;", "\n")
        .replace("&amp;ndash;", "–")
        .replace("&amp;", "&")
}
