package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.entity.MovieEntity

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        name = name,
        coverUrl = coverUrl,
        backdropPath = backdropPath,
        genres = genres?.map { it },
        originCountry = originCountry,
        overview = overview,
        popularity = popularity,
        releaseDate = releaseDate,
        revenue = revenue,
        runtime = runtime,
        status = status,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        voteAverage = voteAverage,
        voteCount = voteCount,
        watchStatus = watchStatus,
    )
}
