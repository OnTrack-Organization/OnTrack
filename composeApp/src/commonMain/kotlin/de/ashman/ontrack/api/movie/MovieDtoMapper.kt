package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.formatCreatorDate
import de.ashman.ontrack.api.getTMDBCoverUrl
import de.ashman.ontrack.api.movie.dto.CollectionResponseDto
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.domain.Collection
import de.ashman.ontrack.domain.Director
import de.ashman.ontrack.domain.Movie

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.toString(),
        title = title,
        coverUrl = posterPath.getTMDBCoverUrl(),
        releaseYear = releaseDate?.take(4),
        description = overview,
        genres = genres?.map { it.name },
        popularity = popularity,
        runtime = runtime,
        voteAverage = voteAverage,
        voteCount = voteCount,
    )
}

fun PersonDetailsDto.toDomain(): Director {
    return Director(
        id = id.toString(),
        name = name.orEmpty(),
        imageUrl = profile_path.getTMDBCoverUrl(),
        birthDate = birthday?.formatCreatorDate(),
        deathDate = deathday?.formatCreatorDate(),
        bio = biography?.ifBlank { null },
    )
}

fun CollectionResponseDto.toDomain(): Collection {
    return Collection(
        id = id,
        name = name,
        imageUrl = posterPath.getTMDBCoverUrl(),
        movies = parts.map { it.toDomain() },
    )
}