package de.ashman.ontrack.movie.api

import de.ashman.ontrack.movie.model.Movie
import de.ashman.ontrack.movie.model.MovieDto
import de.ashman.ontrack.show.model.Show
import de.ashman.ontrack.show.model.ShowDto

fun MovieDto.toDomain(): Movie {
    return Movie(
        adult = adult,
        backdropPath = backdropPath,
        id = id,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount,
    )
}