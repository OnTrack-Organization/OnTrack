package de.ashman.ontrack.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import de.ashman.ontrack.domain.media.MediaType
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.album
import ontrack.app.composeapp.generated.resources.album_network_error
import ontrack.app.composeapp.generated.resources.boardgame
import ontrack.app.composeapp.generated.resources.boardgame_network_error
import ontrack.app.composeapp.generated.resources.book
import ontrack.app.composeapp.generated.resources.book_network_error
import ontrack.app.composeapp.generated.resources.movie
import ontrack.app.composeapp.generated.resources.movie_network_error
import ontrack.app.composeapp.generated.resources.playingcards
import ontrack.app.composeapp.generated.resources.playingcards_outlined
import ontrack.app.composeapp.generated.resources.show
import ontrack.app.composeapp.generated.resources.show_filled
import ontrack.app.composeapp.generated.resources.show_network_error
import ontrack.app.composeapp.generated.resources.show_outlined
import ontrack.app.composeapp.generated.resources.videogame
import ontrack.app.composeapp.generated.resources.videogame_network_error
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.vectorResource

data class MediaTypeUi(
    val icon: ImageVector,
    val outlinedIcon: ImageVector,
    val title: PluralStringResource,
    val error: StringResource,
)

@Composable
fun MediaType.getMediaTypeUi(): MediaTypeUi {
    return when (this) {
        MediaType.MOVIE -> MediaTypeUi(
            icon = Icons.Default.Movie,
            outlinedIcon = Icons.Outlined.Movie,
            title = Res.plurals.movie,
            error = Res.string.movie_network_error,
        )

        MediaType.SHOW -> MediaTypeUi(
            icon = vectorResource(Res.drawable.show_filled),
            outlinedIcon = vectorResource(Res.drawable.show_outlined),
            title = Res.plurals.show,
            error = Res.string.show_network_error,
        )

        MediaType.BOOK -> MediaTypeUi(
            icon = Icons.Default.AutoStories,
            outlinedIcon = Icons.Outlined.AutoStories,
            title = Res.plurals.book,
            error = Res.string.book_network_error,
        )

        MediaType.VIDEOGAME -> MediaTypeUi(
            icon = Icons.Default.SportsEsports,
            outlinedIcon = Icons.Outlined.SportsEsports,
            title = Res.plurals.videogame,
            error = Res.string.videogame_network_error,
        )

        MediaType.BOARDGAME -> MediaTypeUi(
            icon = vectorResource(Res.drawable.playingcards),
            outlinedIcon = vectorResource(Res.drawable.playingcards_outlined),
            title = Res.plurals.boardgame,
            error = Res.string.boardgame_network_error,
        )

        MediaType.ALBUM -> MediaTypeUi(
            icon = Icons.Default.Album,
            outlinedIcon = Icons.Outlined.Album,
            title = Res.plurals.album,
            error = Res.string.album_network_error,
        )
    }
}

suspend fun MediaType.getSingularTitle(): String {
    return getPluralString(
        when (this) {
            MediaType.MOVIE -> Res.plurals.movie
            MediaType.SHOW -> Res.plurals.show
            MediaType.BOOK -> Res.plurals.book
            MediaType.VIDEOGAME -> Res.plurals.videogame
            MediaType.BOARDGAME -> Res.plurals.boardgame
            MediaType.ALBUM -> Res.plurals.album
        },
        1
    )
}
