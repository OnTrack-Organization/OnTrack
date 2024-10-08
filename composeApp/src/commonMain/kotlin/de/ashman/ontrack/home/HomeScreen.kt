package de.ashman.ontrack.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.model.AudioFile
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.ui.audio.AudioPlayerView
import coil3.compose.AsyncImage
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.media.album.ui.AlbumViewModel
import de.ashman.ontrack.media.boardgame.ui.BoardGameViewModel
import de.ashman.ontrack.media.book.ui.BookViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    goToDetail: (String) -> Unit,
    albumViewModel: AlbumViewModel = koinInject(),
) {
    ApiTest(
        modifier = modifier.padding(16.dp),
        goToDetail = goToDetail,
    )
    /* Column(modifier.clickable { goToDetail(1234) }) {
         Text("Home")
     }*/
   /* val state by albumViewModel.uiState.collectAsState()

    LazyColumn {
        item {
            Text("SELECTED")
            Text(state.selectedAlbum.toString())

            if (state.selectedAlbum != null) {
                AudioPlayerView(
                    audios = state.selectedAlbum!!.tracks.map { AudioFile(it.previewUrl, it.name) },
                    audioPlayerConfig = AudioPlayerConfig(
                        isControlsVisible = false,
                    )
                )
            }
        }

        items(state.albums) {

            Text(it.name)
            it.artists.forEach {
                Text(it)
            }
            AsyncImage(
                modifier = modifier.clickable { albumViewModel.fetchAlbumDetails(it.id) },
                model = it.imageUrl,
                contentDescription = "Poster"
            )
            *//*Text(text = it.key ?: "Get Description", modifier.clickable { bookViewModel.fetchBookDetails(it) })
            AsyncImage(
                model = it.coverUrl,
                contentDescription = "Poster"
            )*//*
        }
    }*/
}
