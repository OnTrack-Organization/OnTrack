package de.ashman.ontrack.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.playingcards
import ontrack.composeapp.generated.resources.shelf_title_boardgames
import ontrack.composeapp.generated.resources.shelf_title_books
import ontrack.composeapp.generated.resources.shelf_title_movies
import ontrack.composeapp.generated.resources.shelf_title_music
import ontrack.composeapp.generated.resources.shelf_title_shows
import ontrack.composeapp.generated.resources.shelf_title_videogames
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ShelfScreen(
    modifier: Modifier = Modifier,
    goToShelf: (MediaType) -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MediaType.entries.forEach { mediaType ->
            MediaCard(
                modifier = Modifier.weight(1f),
                mediaType = mediaType,
                goToShelf = goToShelf,
            )
        }
    }
}

@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    mediaType: MediaType,
    goToShelf: (MediaType) -> Unit = {},
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

                if (mediaType.statusTypes != null) {
                    MediaCountRow(mediaType.statusTypes)
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
private fun MediaCountRow(statusTypes: List<StatusType>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        statusTypes.forEach {

                MediaCount(
                    count = 0,
                    statusType = it
                )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MediaCount(
    count: Int,
    statusType: StatusType,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(imageVector = statusType.icon(), contentDescription = statusType.iconDescription)
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

enum class StatusType(
    val title: String,
    val icon: @Composable () -> ImageVector,
    val iconDescription: String,
) {
    // CURRENT
    BINGING(
        title = "Binging",
        icon = { Icons.Outlined.Visibility },
        iconDescription = "Binging Icon"
    ),
    PLAYING(
        title = "Playing",
        icon = { Icons.Outlined.Visibility },
        iconDescription = "Watching Icon"
    ),
    READING(
        title = "Reading",
        icon = { Icons.Outlined.Visibility },
        iconDescription = "Watching Icon"
    ),

    // DONE
    WATCHED(
        title = "Watched",
        icon = { Icons.Outlined.Done },
        iconDescription = "Watched Icon"
    ),
    BINGED(
        title = "Binged",
        icon = { Icons.Outlined.Done },
        iconDescription = "Binged Icon"
    ),
    READ(
        title = "Read",
        icon = { Icons.Outlined.Done },
        iconDescription = "Read Icon"
    ),
    PLAYED(
        title = "Played",
        icon = { Icons.Outlined.Done },
        iconDescription = "Played Icon"
    ),

    // DROPPED
    DROPPED(
        title = "Dropped",
        icon = { Icons.Outlined.VisibilityOff },
        iconDescription = "Dropped Icon"
    ),

    // CATALOG
    CATALOG(
        title = "Catalog",
        icon = { Icons.Outlined.BookmarkAdd },
        iconDescription = "Catalog Icon"
    ),
}

enum class MediaType(
    val title: StringResource,
    val icon: @Composable () -> ImageVector,
    val iconDescription: String,
    val statusTypes: List<StatusType>? = null,
) {
    MOVIES(
        title = Res.string.shelf_title_movies,
        icon = { Icons.Default.Movie },
        iconDescription = "Movie Icon",
        statusTypes = listOf(StatusType.WATCHED, StatusType.CATALOG, StatusType.DROPPED)
    ),
    SHOWS(
        title = Res.string.shelf_title_shows,
        icon = { Icons.Default.Tv },
        iconDescription = "Show Icon",
        statusTypes = listOf(StatusType.BINGED, StatusType.CATALOG, StatusType.DROPPED, StatusType.BINGING)
    ),
    BOOKS(
        title = Res.string.shelf_title_books,
        icon = { Icons.Default.AutoStories },
        iconDescription = "Book Icon",
        statusTypes = listOf(StatusType.READ, StatusType.CATALOG, StatusType.DROPPED, StatusType.READING)
    ),
    VIDEOGAMES(
        title = Res.string.shelf_title_videogames,
        icon = { Icons.Default.SportsEsports },
        iconDescription = "Videogame Icon",
        statusTypes = listOf(StatusType.PLAYED, StatusType.CATALOG, StatusType.DROPPED, StatusType.PLAYING)
    ),
    BOARDGAMES(
        title = Res.string.shelf_title_boardgames,
        icon = { vectorResource(Res.drawable.playingcards) },
        iconDescription = "Boardgame Icon",
        statusTypes = listOf(StatusType.PLAYED, StatusType.CATALOG)
    ),
    MUSIC(
        title = Res.string.shelf_title_music,
        icon = { Icons.Default.Album },
        iconDescription = "Music Icon",
    ),
}