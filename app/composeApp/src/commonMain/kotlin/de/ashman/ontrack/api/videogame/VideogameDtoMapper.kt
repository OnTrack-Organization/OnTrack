package de.ashman.ontrack.api.videogame

import de.ashman.ontrack.api.utils.getIGDBCoverUrl
import de.ashman.ontrack.api.videogame.dto.FranchiseDto
import de.ashman.ontrack.api.videogame.dto.PlatformDto
import de.ashman.ontrack.api.videogame.dto.VideogameDto
import de.ashman.ontrack.domain.media.Franchise
import de.ashman.ontrack.domain.media.Platform
import de.ashman.ontrack.domain.media.Videogame
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun VideogameDto.toDomain(): Videogame =
    Videogame(
        id = id.toString(),
        title = name,
        coverUrl = cover?.url.getIGDBCoverUrl(),
        releaseYear = firstReleaseDate?.let {
            Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.UTC).date.year.toString()
        },
        description = summary,
        genres = genres?.map { it.name },
        involvedCompanies = involvedCompanies?.map { it.company.name },
        platforms = platforms?.map { it.toDomain() },
        apiRating = totalRating,
        apiRatingCount = totalRatingCount,
        similarGames = similarGames?.map { it.toDomain() },
        screenshots = screenshots?.take(10)?.map { it.url.getIGDBCoverUrl() },
    )

fun FranchiseDto.toDomain(): Franchise =
    Franchise(
        id = id,
        name = name,
        games = games?.map { it.toDomain() } ?: emptyList(),
        imageUrl = games?.firstOrNull()?.cover?.url.getIGDBCoverUrl()
    )

fun PlatformDto.toDomain(): Platform =
    Platform(
        id = id,
        abbreviation = abbreviation,
        name = name,
        platformLogo = platformLogo?.url?.getIGDBCoverUrl(),
    )
