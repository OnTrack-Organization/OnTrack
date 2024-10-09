package de.ashman.ontrack.media.boardgame.api

import de.ashman.ontrack.media.boardgame.model.domain.BoardGame
import de.ashman.ontrack.media.boardgame.model.domain.Ratings
import de.ashman.ontrack.media.boardgame.model.entity.BoardGameEntity
import de.ashman.ontrack.media.boardgame.model.entity.RatingsEntity

fun BoardGame.toEntity(): BoardGameEntity {
    return BoardGameEntity(
        id = id,
        name = name,
        coverUrl = coverUrl,
        minAge = minAge,
        yearPublished = yearPublished,
        minPlayers = minPlayers,
        maxPlayers = maxPlayers,
        playingTime = playingTime,
        description = description,
        thumbnail = thumbnail,
        ratings = ratings?.toEntity()
    )
}

fun Ratings.toEntity(): RatingsEntity {
    return RatingsEntity(
        usersRated = usersRated,
        average = average,
        numWeights = numWeights,
        averageWeight = averageWeight
    )
}