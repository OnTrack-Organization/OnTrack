package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.movie.dto.CollectionResponseDto
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.api.utils.formatCreatorDate
import de.ashman.ontrack.api.utils.getTMDBCoverUrl
import de.ashman.ontrack.api.utils.getYear
import de.ashman.ontrack.domain.Director
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.MovieCollection

fun MovieDto.toDomain(): Movie =
    Movie(
        id = id.toString(),
        title = title,
        coverUrl = posterPath.getTMDBCoverUrl(),
        releaseYear = releaseDate?.getYear(),
        description = overview?.takeIf { it.isNotEmpty() },
        genres = genres?.takeIf { it.isNotEmpty() }?.map { it.name },
        popularity = popularity,
        runtime = runtime?.takeIf { it > 0 },
        voteAverage = voteAverage,
        voteCount = voteCount,
    )

fun PersonDetailsDto.toDomain(): Director =
    Director(
        id = id.toString(),
        name = name?.takeIf { it.isNotEmpty() },
        imageUrl = profile_path.getTMDBCoverUrl(),
        birthDate = birthday?.formatCreatorDate(),
        deathDate = deathday?.formatCreatorDate(),
        bio = biography?.ifBlank { null },
    )

fun CollectionResponseDto.toDomain(): MovieCollection =
    MovieCollection(
        id = id,
        name = name,
        imageUrl = posterPath.getTMDBCoverUrl(),
        movies = parts?.takeIf { it.isNotEmpty() }?.map { it.toDomain() },
    )