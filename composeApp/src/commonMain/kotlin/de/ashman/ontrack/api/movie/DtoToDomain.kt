package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.getTMDBCoverUrl
import de.ashman.ontrack.media.model.Movie
import de.ashman.ontrack.api.movie.dto.MovieDto

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.toString(),
        name = title,
        coverUrl = posterPath.getTMDBCoverUrl(),
        backdropPath = backdropPath,
        genres = genres?.map { it.name },
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
    )
}
