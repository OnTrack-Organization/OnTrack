package de.ashman.ontrack.detail.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.detail.MediaGenres
import de.ashman.ontrack.detail.MediaRow
import de.ashman.ontrack.media.model.Album
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.Track
import de.ashman.ontrack.navigation.BottomNavItem.Companion.items
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_artist_albums
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlbumDetailContent(
    album: Album,
    onClickItem: (String) -> Unit = {},
) {
    AlbumTracks(
        tracks = album.tracks,
    )

    MediaRow(
        title = stringResource(Res.string.detail_artist_albums),
        otherMedia = album.artistAlbums,
        onClickItem = onClickItem,
    )
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