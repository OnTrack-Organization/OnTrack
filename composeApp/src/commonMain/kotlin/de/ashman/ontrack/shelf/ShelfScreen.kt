package de.ashman.ontrack.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.media.domain.MediaType
import de.ashman.ontrack.shelf.ui.AlbumViewModel
import de.ashman.ontrack.shelf.ui.BoardGameViewModel
import de.ashman.ontrack.shelf.ui.BookViewModel
import de.ashman.ontrack.media.domain.ConsumeStatus
import de.ashman.ontrack.shelf.ui.MovieViewModel
import de.ashman.ontrack.shelf.ui.ShowViewModel
import de.ashman.ontrack.shelf.ui.VideoGameViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ShelfScreen(
    modifier: Modifier = Modifier,
    goToShelf: (MediaType) -> Unit = {},
    movieViewModel: MovieViewModel = koinInject(),
    showViewModel: ShowViewModel = koinInject(),
    bookViewModel: BookViewModel = koinInject(),
    videoGameViewModel: VideoGameViewModel = koinInject(),
    boardGameViewModel: BoardGameViewModel = koinInject(),
    albumViewModel: AlbumViewModel = koinInject(),
) {
    val movieState by movieViewModel.uiState.collectAsState()
    val showState by showViewModel.uiState.collectAsState()
    val bookState by bookViewModel.uiState.collectAsState()
    val videogameState by videoGameViewModel.uiState.collectAsState()
    val boardgameState by boardGameViewModel.uiState.collectAsState()
    val albumState by albumViewModel.uiState.collectAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MediaType.entries.forEach { mediaType ->
            val counts = when (mediaType) {
                MediaType.MOVIE -> movieState.statusCounts
                MediaType.SHOW -> showState.statusCounts
                MediaType.BOOK -> bookState.statusCounts
                MediaType.VIDEOGAME -> videogameState.statusCounts
                MediaType.BOARDGAME -> boardgameState.statusCounts
                MediaType.ALBUM -> albumState.statusCounts
            }

            MediaCard(
                modifier = Modifier.weight(1f),
                mediaType = mediaType,
                counts = counts,
                goToShelf = goToShelf,
            )
        }
    }
}

@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    mediaType: MediaType,
    counts: Map<ConsumeStatus, Int>,
    goToShelf: (MediaType) -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { goToShelf(mediaType) }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MediaTitle(mediaType.title)

                if (mediaType.consumeStatuses.isNotEmpty()) {
                    MediaCountRow(
                        consumeStatuses = mediaType.consumeStatuses,
                        mediaType = mediaType,
                        counts = counts,
                    )
                }
            }
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = mediaType.icon(),
                contentDescription = stringResource(mediaType.iconDescription)
            )
        }
    }
}

@Composable
private fun MediaCountRow(
    mediaType: MediaType,
    consumeStatuses: List<ConsumeStatus>,
    counts: Map<ConsumeStatus, Int>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        consumeStatuses.forEach {
            MediaCount(
                count = counts[it] ?: 0,
                consumeStatus = it,
                mediaType = mediaType,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MediaCount(
    count: Int,
    consumeStatus: ConsumeStatus,
    mediaType: MediaType
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = consumeStatus.getConsumeStatusIcon(),
            contentDescription = consumeStatus.name
        )
        Text("$count")
    }
}

@Composable
private fun MediaTitle(mediaTitle: StringResource) {
    Text(
        text = stringResource(mediaTitle),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
    )
}
