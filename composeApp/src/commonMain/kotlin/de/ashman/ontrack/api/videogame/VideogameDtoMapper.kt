package de.ashman.ontrack.api.videogame

import de.ashman.ontrack.api.getIGDBCoverUrl
import de.ashman.ontrack.api.videogame.dto.PlatformDto
import de.ashman.ontrack.api.videogame.dto.VideogameDto
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
            Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.UTC).date.year.toString()
        },
        description = summary,
        genres = genres?.map { it.name },
        involvedCompanies = involvedCompanies?.map { it.company.name },
        title = name.orEmpty(),
        platforms = platforms?.map { it.toDomain() },
        totalRating = totalRating,
        totalRatingCount = totalRatingCount,
        similarGames = similarGames?.map { it.toDomain() },
    )
}

/*fun VideogameDto.toFranchiseDomain(): Videogame {
    return Videogame(
        id = id.toString(),
        title = name.orEmpty(),
        coverUrl = cover?.url?.getIGDBCoverUrl().orEmpty(),
    )
}*/

fun PlatformDto.toDomain(): Platform {
    return Platform(
        id = id,
        abbreviation = abbreviation,
        name = name,
        platformLogo = platformLogo?.url?.getIGDBCoverUrl(),
    )
}
