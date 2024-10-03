package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.show.model.domain.Genre
import de.ashman.ontrack.media.show.model.domain.Season
import de.ashman.ontrack.media.show.model.domain.Show
import de.ashman.ontrack.media.show.model.entity.GenreEntity
import de.ashman.ontrack.media.show.model.entity.SeasonEntity
import de.ashman.ontrack.media.show.model.entity.ShowEntity

fun Show.toEntity(): ShowEntity {
    return ShowEntity(
        backdropPath = backdropPath,
        episodeRunTime = episodeRunTime,
        firstAirDate = firstAirDate,
        genres = genres?.map { it.toEntity() },
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
        seasons = seasons?.map { it.toEntity() },
        status = status,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun Genre.toEntity(): GenreEntity {
    return GenreEntity(
        id = id,
        name = name
    )
}

fun Season.toEntity(): SeasonEntity {
    return SeasonEntity(
        airDate = airDate,
        episodeCount = episodeCount,
        id = id,
        name = name,
        overview = overview,
        posterPath = posterPath,
        seasonNumber = seasonNumber,
        voteAverage = voteAverage
    )
}