package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.getTMDBCoverUrl
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.domain.Movie

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.toString(),
        title = title,
        coverUrl = posterPath.getTMDBCoverUrl(),
        releaseYear = releaseDate?.take(4),
        description = overview,
        backdropPath = backdropPath,
        genres = genres?.map { it.name },
        originCountry = originCountry,
        popularity = popularity,
        revenue = revenue,
        runtime = runtime,
        status = status,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        voteAverage = voteAverage,
        voteCount = voteCount,
    )
}
