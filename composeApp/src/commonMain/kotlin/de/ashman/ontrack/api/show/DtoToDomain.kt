package de.ashman.ontrack.api.show

import de.ashman.ontrack.api.getTMDBCoverUrl
import de.ashman.ontrack.media.model.Season
import de.ashman.ontrack.media.model.Show
import de.ashman.ontrack.api.show.dto.SeasonDto
import de.ashman.ontrack.api.show.dto.ShowDto

fun ShowDto.toDomain(): Show {
    return Show(
        id = id.toString(),
        name = name,
        coverUrl = posterPath.getTMDBCoverUrl(),
        releaseYear = firstAirDate?.take(4),
        backdropPath = backdropPath,
        episodeRunTime = episodeRunTime,
        genres = genres?.map { it.name },
        languages = languages,
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        originCountry = originCountry,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
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
        airDate = airDate,
        episodeCount = episodeCount,
        name = name,
        overview = overview,
        posterPath = posterPath,
        seasonNumber = seasonNumber,
        voteAverage = voteAverage
    )
}
