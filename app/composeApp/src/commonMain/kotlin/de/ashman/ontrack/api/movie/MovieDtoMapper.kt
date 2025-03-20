package de.ashman.ontrack.api.movie

import de.ashman.ontrack.api.movie.dto.CollectionResponseDto
import de.ashman.ontrack.api.movie.dto.MovieDto
import de.ashman.ontrack.api.movie.dto.PersonDetailsDto
import de.ashman.ontrack.api.utils.formatDates
import de.ashman.ontrack.api.utils.getTMDBCoverUrl
import de.ashman.ontrack.api.utils.getYear
import de.ashman.ontrack.domain.media.Director
import de.ashman.ontrack.domain.media.Movie
import de.ashman.ontrack.domain.media.MovieCollection

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
        apiRating = voteAverage,
        apiRatingCount = voteCount,
        similarMovies = similar?.movies?.map { it.toDomain() }?.takeIf { it.isNotEmpty() },
        images = images?.backdrops?.take(10)?.map { it.filePath.getTMDBCoverUrl() },
    )

fun PersonDetailsDto.toDomain(): Director =
    Director(
        id = id.toString(),
        name = name?.takeIf { it.isNotEmpty() },
        imageUrl = profile_path.getTMDBCoverUrl(),
        birthDate = birthday?.formatDates(),
        deathDate = deathday?.formatDates(),
        bio = biography?.ifBlank { null },
    )

fun CollectionResponseDto.toDomain(): MovieCollection? =
    parts?.takeIf { it.isNotEmpty() }?.let {
        MovieCollection(
            id = id,
            name = name,
            imageUrl = posterPath.getTMDBCoverUrl(),
            movies = it.map { part -> part.toDomain() }
        )
    }
