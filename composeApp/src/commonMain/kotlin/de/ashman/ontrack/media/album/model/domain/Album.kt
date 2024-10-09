package de.ashman.ontrack.media.album.model.domain

import de.ashman.ontrack.media.Media
import de.ashman.ontrack.media.MediaType
import de.ashman.ontrack.media.StatusType
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    override val id: String,
    override val type: MediaType = MediaType.ALBUM,
    override val name: String,
    override val consumeStatus: StatusType? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    val artists: List<String>,
    val genres: List<String>?,
    val label: String?,
    val popularity: Int?,
    val releaseDate: String,
    val spotifyUrl: String,
    val totalTracks: Int,
    val tracks: List<Track>,
) : Media

@Serializable
data class Track(
    val id: String,
    val artists: List<String>,
    val durationMs: Int,
    val name: String,
    val previewUrl: String,
    val trackNumber: Int,
    val url: String,
)
