package de.ashman.ontrack.api.album

import de.ashman.ontrack.api.album.dto.AlbumDto
import de.ashman.ontrack.api.album.dto.AlbumResponseDto
import de.ashman.ontrack.api.album.dto.ArtistDto
import de.ashman.ontrack.api.album.dto.TrackDto
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Artist
import de.ashman.ontrack.domain.Track
import kotlin.collections.joinToString

fun AlbumDto.toDomain(): Album {
    return Album(
        id = id,
        title = name,
        coverUrl = images.first().url,
        releaseYear = releaseDate.take(4),
        description = tracks?.items?.joinToString(separator = "\n") {
            "${it.trackNumber}. ${it.name}"
        },
        artists = artists.map { it.toDomain() },
        label = label,
        popularity = popularity,
        spotifyUrl = externalUrls.spotify,
        totalTracks = totalTracks,
        tracks = tracks?.items?.map { it.toDomain() } ?: emptyList(),
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

fun ArtistDto.toDomain() : Artist {
    return Artist(
        id = id,
        name = name,
    )
}

fun AlbumResponseDto.toDomain(): List<Album> {
    return this.items.map { it.toDomain() }
}
