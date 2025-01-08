package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    override val id: String,
    override val type: MediaType = MediaType.ALBUM,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
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
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        infoItems.add(releaseDate)
        infoItems.add("$totalTracks Tracks")
        infoItems.add(artists.first())

        return infoItems
    }
}

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
