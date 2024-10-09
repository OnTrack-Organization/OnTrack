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
import de.ashman.ontrack.media.videogame.model.entity.FranchiseEntity
import de.ashman.ontrack.media.videogame.model.entity.PlatformEntity
import de.ashman.ontrack.media.videogame.model.entity.SimilarGameEntity
import de.ashman.ontrack.media.videogame.model.entity.VideoGameEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun VideoGame.toEntity(): VideoGameEntity {
    return VideoGameEntity(
        id = id,
        coverUrl = coverUrl,
        firstReleaseDate = firstReleaseDate,
        franchises = franchises?.map { it.toEntity() },
        genres = genres,
        name = name,
        platforms = platforms?.map { it.toEntity() },
        similarGames = similarGames?.map { it.toEntity() },
        totalRating = totalRating,
        totalRatingCount = totalRatingCount,
        summary = summary
    )
}

fun Franchise.toEntity(): FranchiseEntity {
    return FranchiseEntity(
        name = name,
        games = games.map { it.toEntity() }
    )
}

fun Platform.toEntity(): PlatformEntity {
    return PlatformEntity(
        abbreviation = abbreviation,
        name = name,
        platformLogo = platformLogo
    )
}

fun SimilarGame.toEntity(): SimilarGameEntity {
    return SimilarGameEntity(
        name = name,
        cover = cover
    )
}