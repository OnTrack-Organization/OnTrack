package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.getTMDBCoverUrl
import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.MovieDto

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
