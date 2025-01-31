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
    override val releaseYear: String? = null,
    override val trackStatus: TrackStatus? = null,
    override val ratingStats: RatingStats? = null,
    override val description: String? = null,
    val mainArtist: Artist? = null,
    val label: String? = null,
    val popularity: Int? = null,
    val spotifyUrl: String? = null,
    val totalTracks: Int? = null,
    val tracks: List<Track>? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        totalTracks?.let { infoItems.add(getPluralString(Res.plurals.detail_tracks, it, it)) }

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
    val imageUrl: String? = null,
    val popularity: Int? = null,
    val artistAlbums: List<Album>? = null,
) : CommonParcelable
