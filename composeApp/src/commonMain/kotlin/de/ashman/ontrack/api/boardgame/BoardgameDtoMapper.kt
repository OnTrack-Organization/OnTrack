package de.ashman.ontrack.api.boardgame

import de.ashman.ontrack.api.boardgame.dto.BoardgameDto
import de.ashman.ontrack.api.boardgame.dto.LinkDto
import de.ashman.ontrack.api.boardgame.dto.StatisticsDto
import de.ashman.ontrack.api.utils.decodeHtmlManually
import de.ashman.ontrack.api.utils.nonZeroToInt
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.BoardgameDesigner
import de.ashman.ontrack.domain.Ratings

fun BoardgameDto.toDomain(): Boardgame =
    Boardgame(
        id = id.orEmpty(),
        title = names?.first()?.value.orEmpty(),
        coverUrl = image.orEmpty(),
        releaseYear = yearpublished?.value,
        description = description?.decodeHtmlManually(),
        designer = links?.find { it.type == "boardgamedesigner" }?.toBoardgameDesignerDomain(),
        minPlayers = minplayers?.value?.nonZeroToInt(),
        maxPlayers = maxplayers?.value?.nonZeroToInt(),
        playingTime = playingtime?.value?.nonZeroToInt(),
        ratings = statistics?.ratings?.toDomain(),
        franchise = links?.mapNotNull { it.toBoardgameDomain() },
    )

fun LinkDto.toBoardgameDesignerDomain(): BoardgameDesigner =
    BoardgameDesigner(
        id = id.orEmpty(),
        name = value.orEmpty(),
    )

fun LinkDto.toBoardgameDomain(): Boardgame =
    Boardgame(
        boardgameType = type.orEmpty(),
        id = id.orEmpty(),
        title = value.orEmpty(),
        coverUrl = "",
        description = "",
    )

fun StatisticsDto.RatingsDto.toDomain(): Ratings =
    Ratings(
        usersRated = usersRated.value,
        average = average.value,
        numWeights = numWeights.value,
        averageWeight = averageWeight.value
    )
