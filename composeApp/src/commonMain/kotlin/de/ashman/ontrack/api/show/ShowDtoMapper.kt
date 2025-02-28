package de.ashman.ontrack.api.show

import de.ashman.ontrack.api.show.dto.SeasonDto
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.api.utils.getTMDBCoverUrl
import de.ashman.ontrack.api.utils.getYear
import de.ashman.ontrack.domain.media.Season
import de.ashman.ontrack.domain.media.Show

fun ShowDto.toDomain(): Show =
    Show(
        id = id.toString(),
        title = name,
        coverUrl = posterPath.getTMDBCoverUrl(),
        releaseYear = firstAirDate?.getYear(),
        description = overview?.takeIf { it.isNotEmpty() },
        genres = genres?.takeIf { it.isNotEmpty() }?.map { it.name },
        numberOfEpisodes = numberOfEpisodes?.takeIf { it > 0 },
        numberOfSeasons = numberOfSeasons?.takeIf { it > 0 },
        popularity = popularity,
        seasons = seasons?.map { it.toDomain() },
        apiRating = voteAverage,
        apiRatingCount = voteCount,
    )

fun SeasonDto.toDomain(): Season =
    Season(
        id = id,
        coverUrl = posterPath.getTMDBCoverUrl(),
        title = name,
        description = overview,
        releaseYear = airDate?.getYear(),
        seasonNumber = seasonNumber,
        episodeCount = episodeCount,
        voteAverage = voteAverage,
    )
