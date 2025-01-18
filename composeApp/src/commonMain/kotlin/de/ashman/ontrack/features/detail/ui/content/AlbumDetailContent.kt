package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Track
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_artist_albums
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.AlbumDetailContent(
    album: Album,
    onClickItem: (String) -> Unit = {},
) {
    item {
        AlbumTracks(
            tracks = album.tracks,
        )
    }

    item {
        MediaRow(
            title = stringResource(Res.string.detail_artist_albums),
            otherMedia = album.artistAlbums,
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
        tracks.forEach {
            Text("${it.trackNumber}. ${it.name}")
        }
    }
}