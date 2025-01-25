package de.ashman.ontrack.api.show

import de.ashman.ontrack.api.getTMDBCoverUrl
import de.ashman.ontrack.api.show.dto.SeasonDto
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.domain.Season
import de.ashman.ontrack.domain.Show

fun ShowDto.toDomain(): Show {
    return Show(
        id = id.toString(),
        title = name,
        coverUrl = posterPath.getTMDBCoverUrl(),
        releaseYear = firstAirDate?.take(4),
        description = overview,
        backdropPath = backdropPath,
        episodeRunTime = episodeRunTime,
        genres = genres?.map { it.name },
        languages = languages,
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        originCountry = originCountry,
        originalLanguage = originalLanguage,
        originalName = originalName,
        popularity = popularity,
        seasons = seasons?.map { it.toDomain() },
        status = status,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun SeasonDto.toDomain(): Season {
    return Season(
        id = id,
        coverUrl = posterPath.getTMDBCoverUrl(),
        title = name,
        description = overview,
        releaseYear = airDate?.take(4),
        seasonNumber = seasonNumber,
        episodeCount = episodeCount,
        voteAverage = voteAverage,
    )
}
