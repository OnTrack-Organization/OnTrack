package de.ashman.ontrack.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import de.ashman.ontrack.domain.sub.MediaType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.album
import ontrack.composeapp.generated.resources.boardgame
import ontrack.composeapp.generated.resources.book
import ontrack.composeapp.generated.resources.movie
import ontrack.composeapp.generated.resources.playingcards
import ontrack.composeapp.generated.resources.search_empty_albums
import ontrack.composeapp.generated.resources.search_empty_boardgames
import ontrack.composeapp.generated.resources.search_empty_books
import ontrack.composeapp.generated.resources.search_empty_movies
import ontrack.composeapp.generated.resources.search_empty_shows
import ontrack.composeapp.generated.resources.search_empty_videogames
import ontrack.composeapp.generated.resources.search_placeholder_albums
import ontrack.composeapp.generated.resources.search_placeholder_boardgames
import ontrack.composeapp.generated.resources.search_placeholder_books
import ontrack.composeapp.generated.resources.search_placeholder_movies
import ontrack.composeapp.generated.resources.search_placeholder_shows
import ontrack.composeapp.generated.resources.search_placeholder_videogames
import ontrack.composeapp.generated.resources.show
import ontrack.composeapp.generated.resources.videogame
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

data class MediaTypeUi(
    val icon: ImageVector,
    val title: PluralStringResource,
    val searchPlaceholder: StringResource,
    val emptySearch: StringResource,
)

@Composable
fun MediaType.getMediaTypeUi(): MediaTypeUi {
    return when (this) {
        MediaType.MOVIE -> MediaTypeUi(
            icon = Icons.Default.Movie,
            title = Res.plurals.movie,
            searchPlaceholder = Res.string.search_placeholder_movies,
            emptySearch = Res.string.search_empty_movies,
        )

        MediaType.SHOW -> MediaTypeUi(
            icon = Icons.Default.Tv,
            title = Res.plurals.show,
            searchPlaceholder = Res.string.search_placeholder_shows,
            emptySearch = Res.string.search_empty_shows,
        )

        MediaType.BOOK -> MediaTypeUi(
            icon = Icons.Default.AutoStories,
            title = Res.plurals.book,
            searchPlaceholder = Res.string.search_placeholder_books,
            emptySearch = Res.string.search_empty_books,
        )

        MediaType.VIDEOGAME -> MediaTypeUi(
            icon = Icons.Default.SportsEsports,
            title = Res.plurals.videogame,
            searchPlaceholder = Res.string.search_placeholder_videogames,
            emptySearch = Res.string.search_empty_videogames,
        )

        MediaType.BOARDGAME -> MediaTypeUi(
            icon = vectorResource(Res.drawable.playingcards),
            title = Res.plurals.boardgame,
            searchPlaceholder = Res.string.search_placeholder_boardgames,
            emptySearch = Res.string.search_empty_boardgames,
        )

        MediaType.ALBUM -> MediaTypeUi(
            icon = Icons.Default.Album,
            title = Res.plurals.album,
            searchPlaceholder = Res.string.search_placeholder_albums,
            emptySearch = Res.string.search_empty_albums,
        )
    }
}
