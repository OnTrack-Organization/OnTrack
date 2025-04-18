package de.ashman.ontrack.api.show

import de.ashman.ontrack.api.movie.getShowDetailUrl
import de.ashman.ontrack.api.movie.getTMDBCoverUrl
import de.ashman.ontrack.api.movie.getYear
import de.ashman.ontrack.api.show.dto.SeasonDto
import de.ashman.ontrack.api.show.dto.ShowDto
import de.ashman.ontrack.domain.media.Season
import de.ashman.ontrack.domain.media.Show

fun ShowDto.toDomain(): Show =
    Show(
        id = id.toString(),
        title = name,
        coverUrl = posterPath.getTMDBCoverUrl(),
        detailUrl = getShowDetailUrl(id),
        releaseYear = firstAirDate?.getYear(),
        description = overview?.takeIf { it.isNotEmpty() },
        genres = genres?.takeIf { it.isNotEmpty() }?.map { it.name },
        numberOfEpisodes = numberOfEpisodes?.takeIf { it > 0 },
        numberOfSeasons = numberOfSeasons?.takeIf { it > 0 },
        popularity = popularity,
        seasons = seasons?.map { it.toDomain() },
        apiRating = voteAverage,
        apiRatingCount = voteCount,
        similarShows = similar?.shows?.map { it.toDomain() }?.takeIf { it.isNotEmpty() },
        images = images?.backdrops?.take(10)?.map { it.filePath.getTMDBCoverUrl() },
    )

fun SeasonDto.toDomain(): Season =
    Season(
        id = id,
        coverUrl = posterPath.getTMDBCoverUrl(),
        title = name,
        description = overview,
        releaseYear = airDate?.getYear(),
        seasonNumber = seasonNumber,
        episodeCount = episodeCount,
        voteAverage = voteAverage,
    )
