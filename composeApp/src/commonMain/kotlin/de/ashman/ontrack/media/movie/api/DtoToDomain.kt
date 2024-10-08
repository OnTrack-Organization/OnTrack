package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.movie.model.domain.Collection
import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.CollectionDto
import de.ashman.ontrack.media.movie.model.dto.MovieDto

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.toString(),
        backdropPath = backdropPath,
        collection = belongsToCollection?.toDomain(),
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

fun CollectionDto.toDomain(): Collection {
    return Collection(
        id = id,
        name = name,
        backdropPath = backdropPath,
        posterPath = posterPath
    )
}
