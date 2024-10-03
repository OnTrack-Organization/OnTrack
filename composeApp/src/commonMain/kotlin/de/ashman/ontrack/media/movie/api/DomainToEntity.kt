package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.common.mapper.toEntity
import de.ashman.ontrack.media.movie.model.domain.Collection
import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.entity.CollectionEntity
import de.ashman.ontrack.media.movie.model.entity.MovieEntity

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        backdropPath = backdropPath,
        collection = collection?.toEntity(),
        genres = genres?.map { it.toEntity() },
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
        watchStatus = watchStatus,
    )
}

fun Collection.toEntity(): CollectionEntity {
    return CollectionEntity(
        id = id,
        name = name,
        backdropPath = backdropPath,
        posterPath = posterPath
    )
}