package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.show.model.Show
import de.ashman.ontrack.media.show.model.ShowDto

fun ShowDto.toDomain(): Show {
    return Show(
        id = id,
        name = name,
        originalName = originalName,
        originalLanguage = originalLanguage,
        isAdult = adult,
        backdropPath = backdropPath,
        genreIds = genreIds,
        originCountry = originCountry,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}