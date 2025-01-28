package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Artist
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_artist
import ontrack.composeapp.generated.resources.detail_artist_albums
import ontrack.composeapp.generated.resources.detail_popularity
import ontrack.composeapp.generated.resources.detail_tracks
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.AlbumDetailContent(
    album: Album,
    onClickItem: (Media) -> Unit = {},
) {
    item {
        ArtistCard(artist = album.mainArtist)
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

@Composable
fun ArtistCard(artist: Artist) {
    val painter = rememberAsyncImagePainter(artist.imageUrl)

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.detail_artist),
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                val state = painter.state.collectAsState().value

                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = painter,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Artist Image",
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            imageVector = Icons.Default.Person,
                            contentDescription = "No Image",
                        )
                    }

                    else -> {}
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )

                artist.popularity?.let {
                    Text(
                        text = stringResource(Res.string.detail_popularity, artist.popularity),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
