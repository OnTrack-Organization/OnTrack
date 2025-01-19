package de.ashman.ontrack.domain.sub

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.album
import ontrack.composeapp.generated.resources.boardgame
import ontrack.composeapp.generated.resources.book
import ontrack.composeapp.generated.resources.movie
import ontrack.composeapp.generated.resources.playingcards
import ontrack.composeapp.generated.resources.show
import ontrack.composeapp.generated.resources.videogame
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
enum class MediaType {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM
}

data class MediaTypeUi(
    val title: PluralStringResource,
    val icon: @Composable () -> ImageVector,
)

fun getMediaTypeUi(mediaType: MediaType): MediaTypeUi {
    return when (mediaType) {
        MediaType.MOVIE -> MediaTypeUi(Res.plurals.movie, { Icons.Default.Movie })
        MediaType.SHOW -> MediaTypeUi(Res.plurals.show, { Icons.Default.Tv })
        MediaType.BOOK -> MediaTypeUi(Res.plurals.book, { Icons.Default.AutoStories })
        MediaType.VIDEOGAME -> MediaTypeUi(Res.plurals.videogame, { Icons.Default.SportsEsports })
        MediaType.BOARDGAME -> MediaTypeUi(Res.plurals.boardgame, { vectorResource(Res.drawable.playingcards) })
        MediaType.ALBUM -> MediaTypeUi(Res.plurals.album, { Icons.Default.Album })
    }
}