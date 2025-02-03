package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaDescription
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_artist
import ontrack.composeapp.generated.resources.detail_artist_albums
import ontrack.composeapp.generated.resources.detail_artist_popularity
import ontrack.composeapp.generated.resources.detail_tracks
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.AlbumDetailContent(
    album: Album,
    onClickItem: (Media) -> Unit = {},
) {
    item {
        MediaDescription(
            title = album.albumTracks?.size?.let { pluralStringResource(Res.plurals.detail_tracks, it, it) },
            description = album.description,
        )
    }

    item {
        CreatorCard(
            title = Res.string.detail_artist,
            name = album.mainArtist?.name,
            imageUrl = album.mainArtist?.imageUrl,
            subInfo = album.mainArtist?.popularity?.let { stringResource(Res.string.detail_artist_popularity, it) },
        )
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_artist_albums),
            items = album.mainArtist?.artistAlbums,
            onClickItem = onClickItem,
        )
    }
}
