package de.ashman.ontrack.api.boardgame

import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.api.boardgame.dto.BoardgameDto
import de.ashman.ontrack.api.boardgame.dto.LinkDto
import de.ashman.ontrack.domain.Ratings
import de.ashman.ontrack.api.boardgame.dto.StatisticsDto

fun BoardgameDto.toDomain(): Boardgame {
    return Boardgame(
        id = id.orEmpty(),
        title = names?.find { it.type == "primary" }?.value ?: names?.first()?.value.orEmpty(),
        coverUrl = image.orEmpty(),
        releaseYear = yearpublished?.value,
        minAge = minage?.value,
        minPlayers = minplayers?.value,
        maxPlayers = maxplayers?.value,
        playingTime = playingtime?.value,
        description = description?.decodeHtmlManually(),
        thumbnail = thumbnail,
        ratings = statistics?.ratings?.toDomain(),
        franchiseItems = links?.map { it.toDomain() },
    )
}

fun LinkDto.toDomain(): Boardgame {
    return Boardgame(
        boardgameType = type.orEmpty(),
        id = id.orEmpty(),
        title = value.orEmpty(),
        coverUrl = "",
        description = "",
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
        .replace(Regex("&nbsp;"), " ")
        .replace(Regex("&amp;#10;"), "\n")
        .replace(Regex("&#10;"), "\n")
        .replace(Regex("&ndash;"), "–")
        .replace(Regex("&mdash;"), "—")
        .replace(Regex("&amp;"), "&")
        .replace(Regex("&quot;"), "\"")
        .replace(Regex("—description from the publisher\\n{2}"), "")
        .trimEnd()
}

