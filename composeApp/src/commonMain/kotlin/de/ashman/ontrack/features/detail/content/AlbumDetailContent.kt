package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_artist_albums
import ontrack.composeapp.generated.resources.detail_tracks
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.AlbumDetailContent(
    album: Album,
    onClickItem: (Media) -> Unit = {},
) {
    item {
        MediaDescription(
            title = pluralStringResource(Res.plurals.detail_tracks, album.tracks.size, album.tracks.size),
            description = album.description,
        )
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_artist_albums),
            items = album.artistAlbums,
            onClickItem = onClickItem,
        )
    }
}
