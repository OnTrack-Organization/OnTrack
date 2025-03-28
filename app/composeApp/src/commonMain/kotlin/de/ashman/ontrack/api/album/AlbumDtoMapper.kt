package de.ashman.ontrack.api.album

import de.ashman.ontrack.api.album.dto.AlbumDto
import de.ashman.ontrack.api.album.dto.AlbumResponseDto
import de.ashman.ontrack.api.album.dto.ArtistDto
import de.ashman.ontrack.api.album.dto.TrackDto
import de.ashman.ontrack.api.movie.getYear
import de.ashman.ontrack.domain.media.Album
import de.ashman.ontrack.domain.media.AlbumTrack
import de.ashman.ontrack.domain.media.Artist

fun AlbumDto.toDomain(): Album =
    Album(
        id = id,
        title = name,
        coverUrl = images.first().url,
        detailUrl = externalUrls.spotify,
        releaseYear = releaseDate.getYear(),
        description = tracks?.items?.toNumberedTracks(),
        mainArtist = artists.first().toDomain(),
        label = label,
        apiRating = popularity?.toDouble(),
        spotifyUrl = externalUrls.spotify,
        totalTracks = totalTracks,
        albumTracks = tracks?.items?.map { it.toDomain() } ?: emptyList(),
    )

fun TrackDto.toDomain(): AlbumTrack =
    AlbumTrack(
        id = id,
        artists = artists.map { it.name },
        durationMs = durationMs,
        name = name,
        previewUrl = previewUrl,
        trackNumber = trackNumber,
        url = externalUrls.spotify
    )

fun ArtistDto.toDomain(): Artist =
    Artist(
        id = id,
        name = name,
        popularity = popularity,
        imageUrl = images?.firstOrNull()?.url,
    )

fun AlbumResponseDto.toDomain(): List<Album> = items.map { it.toDomain() }
