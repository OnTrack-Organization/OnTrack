package de.ashman.ontrack.media.model

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    override val id: String,
    override val mediaType: MediaType = MediaType.ALBUM,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    override val releaseYear: String?,
    val artists: List<String>,
    val genres: List<String>?,
    val label: String?,
    val popularity: Int?,
    val spotifyUrl: String,
    val totalTracks: Int,
    val tracks: List<Track>,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        infoItems.add("$totalTracks Tracks")
        infoItems.add(artists.first())

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class Track(
    val id: String,
    val artists: List<String>,
    val durationMs: Int,
    val name: String,
    val previewUrl: String,
    val trackNumber: Int,
    val url: String,
) : CommonParcelable
