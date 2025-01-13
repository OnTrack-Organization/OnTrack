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
import ontrack.composeapp.generated.resources.media_albums
import ontrack.composeapp.generated.resources.media_boardgames
import ontrack.composeapp.generated.resources.media_books
import ontrack.composeapp.generated.resources.media_movies
import ontrack.composeapp.generated.resources.media_shows
import ontrack.composeapp.generated.resources.media_videogames
import ontrack.composeapp.generated.resources.playingcards
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
enum class MediaType(
    val title: StringResource,
    val icon: @Composable () -> ImageVector,
    val consumeStatuses: List<ConsumeStatus> = emptyList()
) {
    @SerialName("movie")
    MOVIE(
        title = Res.string.media_movies,
        icon = { Icons.Default.Movie },
        consumeStatuses = listOf(
            ConsumeStatus.WATCHED,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("show")
    SHOW(
        title = Res.string.media_shows,
        icon = { Icons.Default.Tv },
        consumeStatuses = listOf(
            ConsumeStatus.BINGED,
            ConsumeStatus.BINGING,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("book")
    BOOK(
        title = Res.string.media_books,
        icon = { Icons.Default.AutoStories },
        consumeStatuses = listOf(
            ConsumeStatus.READ,
            ConsumeStatus.READING,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("videogame")
    VIDEOGAME(
        title = Res.string.media_videogames,
        icon = { Icons.Default.SportsEsports },
        consumeStatuses = listOf(
            ConsumeStatus.PLAYED,
            ConsumeStatus.PLAYING,
            ConsumeStatus.DROPPED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("boardgame")
    BOARDGAME(
        title = Res.string.media_boardgames,
        icon = { vectorResource(Res.drawable.playingcards) },
        consumeStatuses = listOf(
            ConsumeStatus.PLAYED,
            ConsumeStatus.CATALOG,
        )
    ),

    @SerialName("album")
    ALBUM(
        title = Res.string.media_albums,
        icon = { Icons.Default.Album },
        consumeStatuses = listOf(
            ConsumeStatus.LISTENED,
            ConsumeStatus.CATALOG,
        )
    );
}
