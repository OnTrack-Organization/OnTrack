package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.common.mapper.toEntity
import de.ashman.ontrack.media.show.model.domain.CreatedBy
import de.ashman.ontrack.media.show.model.domain.LastEpisodeToAir
import de.ashman.ontrack.media.show.model.domain.Network
import de.ashman.ontrack.media.show.model.domain.Season
import de.ashman.ontrack.media.show.model.domain.Show
import de.ashman.ontrack.media.show.model.entity.CreatedByEntity
import de.ashman.ontrack.media.show.model.entity.LastEpisodeToAirEntity
import de.ashman.ontrack.media.show.model.entity.NetworkEntity
import de.ashman.ontrack.media.show.model.entity.SeasonEntity
import de.ashman.ontrack.media.show.model.entity.ShowEntity

fun Show.toEntity(): ShowEntity {
    return ShowEntity(
        adult = adult,
        backdropPath = backdropPath,
        createdBy = createdBy?.map { it.toEntity() },
        episodeRunTime = episodeRunTime,
        firstAirDate = firstAirDate,
        genres = genres?.map { it.toEntity() },
        homepage = homepage,
        id = id,
        inProduction = inProduction,
        languages = languages,
        lastAirDate = lastAirDate,
        lastEpisodeToAir = lastEpisodeToAir?.toEntity(),
        name = name,
        nextEpisodeToAir = nextEpisodeToAir,
        networks = networks?.map { it.toEntity() },
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        originCountry = originCountry,
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCompanies = productionCompanies?.map { it.toEntity() },
        productionCountries = productionCountries?.map { it.toEntity() },
        seasons = seasons?.map { it.toEntity() },
        spokenLanguages = spokenLanguages?.map { it.toEntity() },
        status = status,
        tagline = tagline,
        type = type,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun CreatedBy.toEntity(): CreatedByEntity {
    return CreatedByEntity(
        id = id,
        creditId = creditId,
        name = name,
        originalName = originalName,
        gender = gender,
        profilePath = profilePath
    )
}

fun LastEpisodeToAir.toEntity(): LastEpisodeToAirEntity {
    return LastEpisodeToAirEntity(
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

fun Network.toEntity(): NetworkEntity {
    return NetworkEntity(id = id, logoPath = logoPath, name = name, originCountry = originCountry)
}

fun Season.toEntity(): SeasonEntity {
    return SeasonEntity(
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