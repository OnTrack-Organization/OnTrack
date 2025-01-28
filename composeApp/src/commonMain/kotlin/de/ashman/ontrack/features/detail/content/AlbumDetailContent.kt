package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.CreatorCard
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
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
        CreatorCard(
            title = Res.string.detail_artist,
            name = album.mainArtist.name,
            imageUrl = album.mainArtist.imageUrl,
            subInfo = album.mainArtist.popularity?.let { stringResource(Res.string.detail_artist_popularity, album.mainArtist.popularity) },
        )
    }

    item {
        MediaDescription(
            title = pluralStringResource(Res.plurals.detail_tracks, album.tracks.size, album.tracks.size),
            description = album.description,
        )
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_artist_albums),
            items = album.mainArtist.artistAlbums,
            onClickItem = onClickItem,
        )
    }
}
