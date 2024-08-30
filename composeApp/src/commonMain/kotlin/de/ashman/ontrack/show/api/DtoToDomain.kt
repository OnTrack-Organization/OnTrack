package de.ashman.ontrack.show.api

import de.ashman.ontrack.show.model.Show
import de.ashman.ontrack.show.model.ShowDto

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