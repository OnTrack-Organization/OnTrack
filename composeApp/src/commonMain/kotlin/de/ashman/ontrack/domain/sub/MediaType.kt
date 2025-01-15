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
enum class MediaType {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM
}

data class MediaTypeUi(
    val title: StringResource,
    val icon: @Composable () -> ImageVector,
)

fun getMediaTypeUi(mediaType: MediaType): MediaTypeUi {
    return when (mediaType) {
        MediaType.MOVIE -> MediaTypeUi(Res.string.media_movies, { Icons.Default.Movie })
        MediaType.SHOW -> MediaTypeUi(Res.string.media_shows, { Icons.Default.Tv })
        MediaType.BOOK -> MediaTypeUi(Res.string.media_books, { Icons.Default.AutoStories })
        MediaType.VIDEOGAME -> MediaTypeUi(Res.string.media_videogames, { Icons.Default.SportsEsports })
        MediaType.BOARDGAME -> MediaTypeUi(Res.string.media_boardgames, { vectorResource(Res.drawable.playingcards) })
        MediaType.ALBUM -> MediaTypeUi(Res.string.media_albums, { Icons.Default.Album })
    }
}