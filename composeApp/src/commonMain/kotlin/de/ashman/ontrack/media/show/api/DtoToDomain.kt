package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.common.mapper.toDomain
import de.ashman.ontrack.media.show.model.domain.CreatedBy
import de.ashman.ontrack.media.show.model.domain.LastEpisodeToAir
import de.ashman.ontrack.media.show.model.domain.Network
import de.ashman.ontrack.media.show.model.domain.Season
import de.ashman.ontrack.media.show.model.domain.Show
import de.ashman.ontrack.media.show.model.dto.CreatedByDto
import de.ashman.ontrack.media.show.model.dto.LastEpisodeToAirDto
import de.ashman.ontrack.media.show.model.dto.NetworkDto
import de.ashman.ontrack.media.show.model.dto.SeasonDto
import de.ashman.ontrack.media.show.model.dto.ShowDto

fun ShowDto.toDomain(): Show {
    return Show(
        adult = adult,
        backdropPath = backdropPath,
        createdBy = createdBy?.map { it.toDomain() },
        episodeRunTime = episodeRunTime,
        firstAirDate = firstAirDate,
        genres = genres?.map { it.toDomain() },
        homepage = homepage,
        id = id,
        inProduction = inProduction,
        languages = languages,
        lastAirDate = lastAirDate,
        lastEpisodeToAir = lastEpisodeToAir?.toDomain(),
        name = name,
        nextEpisodeToAir = nextEpisodeToAir,
        networks = networks?.map { it.toDomain() },
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        originCountry = originCountry,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCompanies = productionCompanies?.map { it.toDomain() },
        productionCountries = productionCountries?.map { it.toDomain() },
        seasons = seasons?.map { it.toDomain() },
        spokenLanguages = spokenLanguages?.map { it.toDomain() },
        status = status,
        tagline = tagline,
        type = type,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun CreatedByDto.toDomain(): CreatedBy {
    return CreatedBy(
        id = id,
        creditId = creditId,
        name = name,
        originalName = originalName,
        gender = gender,
        profilePath = profilePath
    )
}

fun LastEpisodeToAirDto.toDomain(): LastEpisodeToAir {
    return LastEpisodeToAir(
        id = id,
        name = name,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        airDate = airDate,
        episodeNumber = episodeNumber,
        episodeType = episodeType,
        productionCode = productionCode,
        runtime = runtime,
        seasonNumber = seasonNumber,
        showId = showId,
        stillPath = stillPath
    )
}

fun NetworkDto.toDomain(): Network {
    return Network(id = id, logoPath = logoPath, name = name, originCountry = originCountry)
}


fun SeasonDto.toDomain(): Season {
    return Season(
        airDate = airDate,
        episodeCount = episodeCount,
        id = id,
        name = name,
        overview = overview,
        posterPath = posterPath,
        seasonNumber = seasonNumber,
        voteAverage = voteAverage
    )
}
