package de.ashman.ontrack.media.movie.api

import de.ashman.ontrack.media.common.mapper.toDomain
import de.ashman.ontrack.media.movie.model.domain.Collection
import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.movie.model.dto.CollectionDto
import de.ashman.ontrack.media.movie.model.dto.MovieDto

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdropPath,
        belongsToCollection = belongsToCollection?.toDomain(),
        budget = budget,
        genres = genres?.map { it.toDomain() },
        homepage = homepage,
        imdbId = imdbId,
        originCountry = originCountry,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCompanies = productionCompanies?.map { it.toDomain() },
        productionCountries = productionCountries?.map { it.toDomain() },
        releaseDate = releaseDate,
        revenue = revenue,
        runtime = runtime,
        status = status,
        tagline = tagline,
        title = title,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        spokenLanguages = spokenLanguages?.map { it.toDomain() },
        video = video,
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
