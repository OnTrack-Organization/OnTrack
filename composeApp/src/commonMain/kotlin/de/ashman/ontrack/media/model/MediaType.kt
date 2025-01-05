package de.ashman.ontrack.media.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.playingcards
import ontrack.composeapp.generated.resources.shelf_title_album
import ontrack.composeapp.generated.resources.shelf_title_boardgames
import ontrack.composeapp.generated.resources.shelf_title_books
import ontrack.composeapp.generated.resources.shelf_title_movies
import ontrack.composeapp.generated.resources.shelf_title_shows
import ontrack.composeapp.generated.resources.shelf_title_videogames
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
enum class MediaType(
    val title: StringResource,
    val icon: @Composable () -> ImageVector,
    val iconDescription: StringResource,
    val consumeStatuses: List<ConsumeStatus> = emptyList()
) {
    @SerialName("movie")
    MOVIE(
        title = Res.string.shelf_title_movies,
        icon = { Icons.Default.Movie },
        iconDescription = Res.string.shelf_title_movies,
        consumeStatuses = listOf(
            ConsumeStatus.WATCHED,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("show")
    SHOW(
        title = Res.string.shelf_title_shows,
        icon = { Icons.Default.Tv },
        iconDescription = Res.string.shelf_title_shows,
        consumeStatuses = listOf(
            ConsumeStatus.BINGED,
            ConsumeStatus.BINGING,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("book")
    BOOK(
        title = Res.string.shelf_title_books,
        icon = { Icons.Default.AutoStories },
        iconDescription = Res.string.shelf_title_books,
        consumeStatuses = listOf(
            ConsumeStatus.READ,
            ConsumeStatus.READING,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("videogame")
    VIDEOGAME(
        title = Res.string.shelf_title_videogames,
        icon = { Icons.Default.SportsEsports },
        iconDescription = Res.string.shelf_title_videogames,
        consumeStatuses = listOf(
            ConsumeStatus.PLAYED,
            ConsumeStatus.PLAYING,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("boardgame")
    BOARDGAME(
        title = Res.string.shelf_title_boardgames,
        icon = { vectorResource(Res.drawable.playingcards) },
        iconDescription = Res.string.shelf_title_boardgames,
        consumeStatuses = listOf(
            ConsumeStatus.PLAYED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("album")
    ALBUM(
        title = Res.string.shelf_title_album,
        icon = { Icons.Default.Album },
        iconDescription = Res.string.shelf_title_album,
        consumeStatuses = listOf(
            ConsumeStatus.LISTENED,
            ConsumeStatus.CATALOG,
        )
    );
}
