package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Track
import de.ashman.ontrack.features.common.MediaRow
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
        AlbumTracks(
            tracks = album.tracks,
        )
    }

    item {
        MediaRow(
            title = stringResource(Res.string.detail_artist_albums),
            items = album.artistAlbums,
            onClickItem = onClickItem,
        )
    }
}

@Composable
fun AlbumTracks(
    tracks: List<Track>,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = pluralStringResource(Res.plurals.detail_tracks, tracks.size, tracks.size),
            style = MaterialTheme.typography.titleMedium,
        )

        tracks.forEach {
            Text("${it.trackNumber}. ${it.name}")
        }
    }
}