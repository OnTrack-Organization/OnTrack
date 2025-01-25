package de.ashman.ontrack.api.videogame

import de.ashman.ontrack.api.getIGDBCoverUrl
import de.ashman.ontrack.api.videogame.dto.FranchiseDto
import de.ashman.ontrack.api.videogame.dto.PlatformDto
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.domain.Franchise
import de.ashman.ontrack.domain.Platform
import de.ashman.ontrack.domain.Videogame
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun VideogameDto.toDomain(): Videogame {
    return Videogame(
        id = id.toString(),
        coverUrl = cover?.url.getIGDBCoverUrl(),
        releaseYear = firstReleaseDate?.let {
            Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.UTC).date.toString().take(4)
        },
        franchises = franchises?.map { it.toDomain() },
        genres = genres?.map { it.name },
        involvedCompanies = involvedCompanies?.map { it.company.name },
        title = name.orEmpty(),
        platforms = platforms?.map { it.toDomain() },
        similarGames = similarGames?.map { it.toDomain() },
        totalRating = totalRating,
        totalRatingCount = totalRatingCount,
        description = summary,
    )
}

fun FranchiseDto.toDomain(): Franchise {
    return Franchise(
        id = id,
        name = name,
    )
}

fun PlatformDto.toDomain(): Platform {
    return Platform(
        id = id,
        abbreviation = abbreviation,
        name = name,
        platformLogo = platformLogo?.url?.getIGDBCoverUrl(),
    )
}
