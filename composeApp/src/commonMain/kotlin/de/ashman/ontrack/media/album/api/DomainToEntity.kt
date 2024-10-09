package de.ashman.ontrack.media.album.api

import de.ashman.ontrack.media.album.model.domain.Album
import de.ashman.ontrack.media.album.model.domain.Track
import de.ashman.ontrack.media.album.model.entity.AlbumEntity
import de.ashman.ontrack.media.album.model.entity.TrackEntity

fun Album.toEntity(): AlbumEntity {
    return AlbumEntity(
        id = id,
        name = name,
        coverUrl = coverUrl,
        artists = artists,
        genres = genres,
        label = label,
        popularity = popularity,
        releaseDate = releaseDate,
        spotifyUrl = spotifyUrl,
        totalTracks = totalTracks,
        tracks = tracks.map { it.toEntity() }
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        artists = artists,
        durationMs = durationMs,
        name = name,
        previewUrl = previewUrl,
        trackNumber = trackNumber,
        url = url
    )
}
