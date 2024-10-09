package de.ashman.ontrack.media.album.model.domain

data class Album(
    val id: String,
    val name: String,
    val coverUrl: String,
    val artists: List<String>,
    val genres: List<String>?,
    val label: String?,
    val popularity: Int?,
    val releaseDate: String,
    val spotifyUrl: String,
    val totalTracks: Int,
    val tracks: List<Track>,
)

data class Track(
    val id: String,
    val artists: List<String>,
    val durationMs: Int,
    val name: String,
    val previewUrl: String,
    val trackNumber: Int,
    val url: String,
)
