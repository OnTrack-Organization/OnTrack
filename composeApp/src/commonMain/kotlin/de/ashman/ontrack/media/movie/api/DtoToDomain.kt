package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.MovieDto

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.toString(),
        backdropPath = backdropPath,
        genres = genres?.map { it.name },
        originCountry = originCountry,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        revenue = revenue,
        runtime = runtime,
        status = status,
        title = title,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        voteAverage = voteAverage,
        voteCount = voteCount,
    )
}
