package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.show.model.domain.Genre
import de.ashman.ontrack.media.show.model.domain.Season
import de.ashman.ontrack.media.show.model.domain.Show
import de.ashman.ontrack.media.show.model.dto.GenreDto
import de.ashman.ontrack.media.show.model.dto.SeasonDto
import de.ashman.ontrack.media.show.model.dto.ShowDto

fun ShowDto.toDomain(): Show {
    return Show(
        backdropPath = backdropPath,
        episodeRunTime = episodeRunTime,
        firstAirDate = firstAirDate,
        genres = genres?.map { it.toDomain() },
        id = id,
        languages = languages,
        name = name,
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        originCountry = originCountry,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        seasons = seasons?.map { it.toDomain() },
        status = status,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
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
