package de.ashman.ontrack.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.OnTrackScreen
import de.ashman.ontrack.media.videogame.ui.VideoGameViewModel
import de.ashman.ontrack.navigation.Route
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    goToDetail: (Int) -> Unit,
    videoGameViewModel: VideoGameViewModel = koinInject()
) {
    ApiTest(
        modifier = modifier.padding(16.dp),
        goToDetail = goToDetail,
    )
   /* Column(modifier.clickable { goToDetail(1234) }) {
        Text("Home")
    }*/
   /* val gameState by videoGameViewModel.uiState.collectAsState()

    LazyColumn {
        items(gameState.games) {
            Text(it.name)
            Text("${it.totalRating} / ${it.totalRatingCount}")
            AsyncImage(
                model = it.coverUrl,
                contentDescription = "Poster",
                modifier = Modifier.clickable {
                    videoGameViewModel.fetchGameDetails(it.id)
                }
            )
            Text(it.firstReleaseDate ?: "")
        }
    }*/
}
