package de.ashman.ontrack.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.media.movie.ui.MovieViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.playingcards
import ontrack.composeapp.generated.resources.shelf_title_boardgames
import ontrack.composeapp.generated.resources.shelf_title_books
import ontrack.composeapp.generated.resources.shelf_title_movies
import ontrack.composeapp.generated.resources.shelf_title_music
import ontrack.composeapp.generated.resources.shelf_title_shows
import ontrack.composeapp.generated.resources.shelf_title_videogames
import ontrack.composeapp.generated.resources.shelves
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject

@Composable
fun ShelfScreen(
    modifier: Modifier = Modifier,
    goToShelf: (MediaType) -> Unit = {},
    movieViewModel: MovieViewModel = koinInject(),
) {
    val uiState by movieViewModel.uiState.collectAsState()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            // TODO aus user viewmodel lieber
            text = "${Firebase.auth.currentUser?.displayName}'s Shelf",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        MediaType.entries.forEach { mediaType ->
            val counts = when (mediaType) {
                MediaType.MOVIES -> uiState.statusCounts
                // Add more cases for each media type
                else -> emptyMap()
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
    counts: Map<StatusType, Int>,
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

                if (mediaType.statusTypes.isNotEmpty()) {
                    MediaCountRow(
                        statusTypes = mediaType.statusTypes,
                        mediaType = mediaType,
                        counts = counts,
                    )
                }
            }
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = mediaType.icon(),
                contentDescription = mediaType.iconDescription
            )
        }
    }
}

@Composable
private fun MediaCountRow(
    mediaType: MediaType,
    statusTypes: List<StatusType>,
    counts: Map<StatusType, Int>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        statusTypes.forEach {
            MediaCount(
                count = counts[it] ?: 0,
                statusType = it,
                mediaType = mediaType,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MediaCount(
    count: Int,
    statusType: StatusType,
    mediaType: MediaType
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(imageVector = mediaType.getStatusIcon(statusType), contentDescription = statusType.name)
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

enum class MediaType(
    val title: StringResource,
    val icon: @Composable () -> ImageVector,
    val iconDescription: String,
    val statusTypes: List<StatusType> = emptyList()
) {
    MOVIES(
        title = Res.string.shelf_title_movies,
        icon = { Icons.Default.Movie },
        iconDescription = "Movie Icon",
        statusTypes = listOf(StatusType.ALL, StatusType.WATCHED, StatusType.CATALOG, StatusType.DROPPED)
    ),
    SHOWS(
        title = Res.string.shelf_title_shows,
        icon = { Icons.Default.Tv },
        iconDescription = "Show Icon",
        statusTypes = listOf(StatusType.ALL, StatusType.BINGED, StatusType.CATALOG, StatusType.DROPPED, StatusType.BINGING)
    ),
    BOOKS(
        title = Res.string.shelf_title_books,
        icon = { Icons.Default.AutoStories },
        iconDescription = "Book Icon",
        statusTypes = listOf(StatusType.ALL, StatusType.READ, StatusType.CATALOG, StatusType.DROPPED, StatusType.READING)
    ),
    VIDEOGAMES(
        title = Res.string.shelf_title_videogames,
        icon = { Icons.Default.SportsEsports },
        iconDescription = "Videogame Icon",
        statusTypes = listOf(StatusType.ALL, StatusType.PLAYED, StatusType.CATALOG, StatusType.DROPPED, StatusType.PLAYING)
    ),
    BOARDGAMES(
        title = Res.string.shelf_title_boardgames,
        icon = { vectorResource(Res.drawable.playingcards) },
        iconDescription = "Boardgame Icon",
        statusTypes = listOf(StatusType.ALL, StatusType.PLAYED, StatusType.CATALOG)
    ),
    MUSIC(
        title = Res.string.shelf_title_music,
        icon = { Icons.Default.Album },
        iconDescription = "Music Icon",
        statusTypes = listOf(StatusType.ALL)
    );

    @Composable
    fun getStatusIcon(statusType: StatusType): ImageVector {
        return when (statusType) {
            StatusType.BINGING, StatusType.PLAYING, StatusType.READING -> Icons.Outlined.Visibility
            StatusType.WATCHED, StatusType.BINGED, StatusType.READ, StatusType.PLAYED -> Icons.Outlined.Done
            StatusType.DROPPED -> Icons.Outlined.VisibilityOff
            StatusType.CATALOG -> Icons.Outlined.BookmarkAdd
            StatusType.ALL -> vectorResource(Res.drawable.shelves)
        }
    }
}