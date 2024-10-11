package de.ashman.ontrack.api.album

import de.ashman.ontrack.media.domain.Album
import de.ashman.ontrack.media.domain.Track
import de.ashman.ontrack.api.album.dto.AlbumDto
import de.ashman.ontrack.api.album.dto.AlbumResponseDto
import de.ashman.ontrack.api.album.dto.TrackDto

fun AlbumDto.toDomain(): Album {
    return Album(
        id = id,
        name = name,
        coverUrl = images.first().url,
        artists = artists.map { it.name },
        genres = genres,
        label = label,
        popularity = popularity,
        releaseDate = releaseDate,
        spotifyUrl = externalUrls.spotify,
        totalTracks = totalTracks,
        tracks = tracks?.items?.map { it.toDomain() } ?: emptyList()
    )
}

fun TrackDto.toDomain(): Track {
    return Track(
        id = id,
        artists = artists.map { it.name },
        durationMs = durationMs,
        name = name,
        previewUrl = previewUrl,
        trackNumber = trackNumber,
        url = externalUrls.spotify
    )
}

fun AlbumResponseDto.toDomain(): List<Album> {
    return this.items.map { it.toDomain() }
}

