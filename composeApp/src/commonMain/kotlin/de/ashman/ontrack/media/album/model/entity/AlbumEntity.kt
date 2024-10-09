package de.ashman.ontrack.media.album.model.entity

import de.ashman.ontrack.media.MediaEntity
import de.ashman.ontrack.media.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class AlbumEntity(
    override val id: String,
    override val type: MediaType = MediaType.ALBUM,
    override val name: String,
    override val coverUrl: String,
    val artists: List<String>,
    val genres: List<String>?,
    val label: String?,
    val popularity: Int?,
    val releaseDate: String,
    val spotifyUrl: String,
    val totalTracks: Int,
    val tracks: List<TrackEntity>,
) : MediaEntity

@Serializable
data class TrackEntity(
    val id: String,
    val artists: List<String>,
    val durationMs: Int,
    val name: String,
    val previewUrl: String,
    val trackNumber: Int,
    val url: String,
)
