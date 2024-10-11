package de.ashman.ontrack.home

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.shelf.ui.AlbumViewModel
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
