package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.MovieDto
import de.ashman.ontrack.media.movie.model.entity.MovieEntity

fun MovieDto.toDomain(): Movie {
    return Movie(
        adult = adult,
        backdropPath = backdropPath,
        id = id,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        runtime = runtime,
        status = status,
        title = title,
        voteAverage = voteAverage,
    )
}

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        adult = adult,
        backdropPath = backdropPath,
        id = id,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        runtime = runtime,
        status = status,
        title = title,
        voteAverage = voteAverage,
        watchStatus = watchStatus
    )
}
