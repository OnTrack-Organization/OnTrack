package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.common.mapper.toEntity
import de.ashman.ontrack.media.movie.model.domain.Collection
import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.entity.CollectionEntity
import de.ashman.ontrack.media.movie.model.entity.MovieEntity

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        adult = adult,
        backdropPath = backdropPath,
        belongsToCollection = belongsToCollection?.toEntity(),
        budget = budget,
        genres = genres?.map { it.toEntity() },
        homepage = homepage,
        imdbId = imdbId,
        originCountry = originCountry,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCompanies = productionCompanies?.map { it.toEntity() },
        productionCountries = productionCountries?.map { it.toEntity() },
        releaseDate = releaseDate,
        revenue = revenue,
        runtime = runtime,
        status = status,
        tagline = tagline,
        title = title,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        spokenLanguages = spokenLanguages?.map { it.toEntity() },
        video = video,
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