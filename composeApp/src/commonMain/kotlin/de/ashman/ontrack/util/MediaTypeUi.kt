package de.ashman.ontrack.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import de.ashman.ontrack.domain.MediaType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.album
import ontrack.composeapp.generated.resources.album_network_error
import ontrack.composeapp.generated.resources.boardgame
import ontrack.composeapp.generated.resources.boardgame_network_error
import ontrack.composeapp.generated.resources.book
import ontrack.composeapp.generated.resources.book_network_error
import ontrack.composeapp.generated.resources.movie
import ontrack.composeapp.generated.resources.movie_network_error
import ontrack.composeapp.generated.resources.playingcards
import ontrack.composeapp.generated.resources.search_empty_albums
import ontrack.composeapp.generated.resources.search_empty_boardgames
import ontrack.composeapp.generated.resources.search_empty_books
import ontrack.composeapp.generated.resources.search_empty_movies
import ontrack.composeapp.generated.resources.search_empty_shows
import ontrack.composeapp.generated.resources.search_empty_videogames
import ontrack.composeapp.generated.resources.show
import ontrack.composeapp.generated.resources.show_network_error
import ontrack.composeapp.generated.resources.videogame
import ontrack.composeapp.generated.resources.videogame_network_error
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

data class MediaTypeUi(
    val icon: ImageVector,
    val title: PluralStringResource,
    val emptySearch: StringResource,
    val error: StringResource,
)

@Composable
fun MediaType.getMediaTypeUi(): MediaTypeUi {
    return when (this) {
        MediaType.MOVIE -> MediaTypeUi(
            icon = Icons.Default.Movie,
            title = Res.plurals.movie,
            emptySearch = Res.string.search_empty_movies,
            error = Res.string.movie_network_error,
        )

        MediaType.SHOW -> MediaTypeUi(
            icon = Icons.Default.Tv,
            title = Res.plurals.show,
            emptySearch = Res.string.search_empty_shows,
            error = Res.string.show_network_error,
        )

        MediaType.BOOK -> MediaTypeUi(
            icon = Icons.Default.AutoStories,
            title = Res.plurals.book,
            emptySearch = Res.string.search_empty_books,
            error = Res.string.book_network_error,
        )

        MediaType.VIDEOGAME -> MediaTypeUi(
            icon = Icons.Default.SportsEsports,
            title = Res.plurals.videogame,
            emptySearch = Res.string.search_empty_videogames,
            error = Res.string.videogame_network_error,
        )

        MediaType.BOARDGAME -> MediaTypeUi(
            icon = vectorResource(Res.drawable.playingcards),
            title = Res.plurals.boardgame,
            emptySearch = Res.string.search_empty_boardgames,
            error = Res.string.boardgame_network_error,
        )

        MediaType.ALBUM -> MediaTypeUi(
            icon = Icons.Default.Album,
            title = Res.plurals.album,
            emptySearch = Res.string.search_empty_albums,
            error = Res.string.album_network_error,
        )
    }
}
