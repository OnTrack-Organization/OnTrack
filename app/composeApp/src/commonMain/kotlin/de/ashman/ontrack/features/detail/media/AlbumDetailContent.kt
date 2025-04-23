package de.ashman.ontrack.features.detail.media

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.media.Album
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_artist
import ontrack.composeapp.generated.resources.detail_artist_albums
import ontrack.composeapp.generated.resources.detail_artist_popularity
import ontrack.composeapp.generated.resources.detail_tracks
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.AlbumDetailContent(
    album: Album,
    onClickItem: (MediaNavigationParam) -> Unit,
) {
    item {
        MediaDescription(
            title = album.albumTracks?.size?.let { pluralStringResource(Res.plurals.detail_tracks, it, it) },
            description = album.description,
        )
    }

    album.mainArtist?.let {
        item {
            CreatorCard(
                title = Res.string.detail_artist,
                name = it.name,
                imageUrl = it.imageUrl,
                subInfo = it.popularity?.let { stringResource(Res.string.detail_artist_popularity, it) },
                description = null,
            )
        }
    }

    album.mainArtist?.artistAlbums?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_artist_albums),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }
}
