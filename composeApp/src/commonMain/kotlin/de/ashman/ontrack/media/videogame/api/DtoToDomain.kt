package de.ashman.ontrack.media.videogame.api

import de.ashman.ontrack.media.videogame.model.domain.Franchise
import de.ashman.ontrack.media.videogame.model.domain.Platform
import de.ashman.ontrack.media.videogame.model.domain.SimilarGame
import de.ashman.ontrack.media.videogame.model.domain.VideoGame
import de.ashman.ontrack.media.videogame.model.dto.FranchiseDto
import de.ashman.ontrack.media.videogame.model.dto.PlatformDto
import de.ashman.ontrack.media.videogame.model.dto.SimilarGameDto
import de.ashman.ontrack.media.videogame.model.dto.VideoGameDto
import de.ashman.ontrack.media.videogame.model.dto.getLargeUrl
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun VideoGameDto.toDomain(): VideoGame {
    return VideoGame(
        id = id.toString(),
        coverUrl = cover?.url?.getLargeUrl(),
        firstReleaseDate = firstReleaseDate?.let {
            Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.UTC).date.toString()
        },
        franchises = franchises?.map { it.toDomain() },
        genres = genres?.map { it.name },
        name = name,
        platforms = platforms?.map { it.toDomain() },
        similarGames = similarGames?.map { it.toDomain() },
        totalRating = totalRating,
        totalRatingCount = totalRatingCount,
        summary = summary
    )
}

fun FranchiseDto.toDomain(): Franchise {
    return Franchise(
        name = name,
        games = games.map { it.toDomain() }
    )
}

fun PlatformDto.toDomain(): Platform {
    return Platform(
        abbreviation = abbreviation,
        name = name,
        platformLogo = platformLogo?.url?.getLargeUrl(),
    )
}

fun SimilarGameDto.toDomain(): SimilarGame {
    return SimilarGame(
        name = name,
        cover = cover?.url?.getLargeUrl()
    )
}