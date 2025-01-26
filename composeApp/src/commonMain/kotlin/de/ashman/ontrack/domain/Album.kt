package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_tracks
import org.jetbrains.compose.resources.getPluralString

@Serializable
data class Album(
    override val id: String,
    override val mediaType: MediaType = MediaType.ALBUM,
    override val title: String,
    override val coverUrl: String,
    override val releaseYear: String?,
    override val trackStatus: TrackStatus? = null,
    override val description: String?,
    val artists: List<Artist>,
    val label: String?,
    val popularity: Int?,
    val spotifyUrl: String,
    val totalTracks: Int,
    val tracks: List<Track>,
    val artistAlbums: List<Album>? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        infoItems.add(getPluralString(Res.plurals.detail_tracks, totalTracks, totalTracks))
        infoItems.add(artists.first().name)

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
    val previewUrl: String?,
    val trackNumber: Int,
    val url: String,
) : CommonParcelable

@CommonParcelize
@Serializable
data class Artist(
    val id: String,
    val name: String,
) : CommonParcelable
