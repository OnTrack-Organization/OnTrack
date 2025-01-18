package de.ashman.ontrack.features.track

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatusType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.album_status_catalog_label
import ontrack.composeapp.generated.resources.album_status_catalog_sublabel
import ontrack.composeapp.generated.resources.album_status_consumed_label
import ontrack.composeapp.generated.resources.album_status_consumed_sublabel
import ontrack.composeapp.generated.resources.album_status_consuming_label
import ontrack.composeapp.generated.resources.album_status_consuming_sublabel
import ontrack.composeapp.generated.resources.album_status_dropped_label
import ontrack.composeapp.generated.resources.album_status_dropped_sublabel
import ontrack.composeapp.generated.resources.boardgame_status_catalog_label
import ontrack.composeapp.generated.resources.boardgame_status_catalog_sublabel
import ontrack.composeapp.generated.resources.boardgame_status_consumed_label
import ontrack.composeapp.generated.resources.boardgame_status_consumed_sublabel
import ontrack.composeapp.generated.resources.boardgame_status_consuming_label
import ontrack.composeapp.generated.resources.boardgame_status_consuming_sublabel
import ontrack.composeapp.generated.resources.boardgame_status_dropped_label
import ontrack.composeapp.generated.resources.boardgame_status_dropped_sublabel
import ontrack.composeapp.generated.resources.book_status_catalog_label
import ontrack.composeapp.generated.resources.book_status_catalog_sublabel
import ontrack.composeapp.generated.resources.book_status_consumed_label
import ontrack.composeapp.generated.resources.book_status_consumed_sublabel
import ontrack.composeapp.generated.resources.book_status_consuming_label
import ontrack.composeapp.generated.resources.book_status_consuming_sublabel
import ontrack.composeapp.generated.resources.book_status_dropped_label
import ontrack.composeapp.generated.resources.book_status_dropped_sublabel
import ontrack.composeapp.generated.resources.movie_status_catalog_label
import ontrack.composeapp.generated.resources.movie_status_catalog_sublabel
import ontrack.composeapp.generated.resources.movie_status_consumed_label
import ontrack.composeapp.generated.resources.movie_status_consumed_sublabel
import ontrack.composeapp.generated.resources.movie_status_consuming_label
import ontrack.composeapp.generated.resources.movie_status_consuming_sublabel
import ontrack.composeapp.generated.resources.movie_status_dropped_label
import ontrack.composeapp.generated.resources.movie_status_dropped_sublabel
import ontrack.composeapp.generated.resources.show_status_catalog_label
import ontrack.composeapp.generated.resources.show_status_catalog_sublabel
import ontrack.composeapp.generated.resources.show_status_consumed_label
import ontrack.composeapp.generated.resources.show_status_consumed_sublabel
import ontrack.composeapp.generated.resources.show_status_consuming_label
import ontrack.composeapp.generated.resources.show_status_consuming_sublabel
import ontrack.composeapp.generated.resources.show_status_dropped_label
import ontrack.composeapp.generated.resources.show_status_dropped_sublabel
import ontrack.composeapp.generated.resources.videogame_status_catalog_label
import ontrack.composeapp.generated.resources.videogame_status_catalog_sublabel
import ontrack.composeapp.generated.resources.videogame_status_consumed_label
import ontrack.composeapp.generated.resources.videogame_status_consumed_sublabel
import ontrack.composeapp.generated.resources.videogame_status_consuming_label
import ontrack.composeapp.generated.resources.videogame_status_consuming_sublabel
import ontrack.composeapp.generated.resources.videogame_status_dropped_label
import ontrack.composeapp.generated.resources.videogame_status_dropped_sublabel
import org.jetbrains.compose.resources.StringResource

@Composable
fun getLabelForStatus(status: TrackStatusType, mediaType: MediaType): StringResource {
    return when (mediaType) {
        MediaType.MOVIE -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.movie_status_consuming_label
            TrackStatusType.CONSUMED -> Res.string.movie_status_consumed_label
            TrackStatusType.DROPPED -> Res.string.movie_status_dropped_label
            TrackStatusType.CATALOG -> Res.string.movie_status_catalog_label
        }

        MediaType.BOOK -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.book_status_consuming_label
            TrackStatusType.CONSUMED -> Res.string.book_status_consumed_label
            TrackStatusType.DROPPED -> Res.string.book_status_dropped_label
            TrackStatusType.CATALOG -> Res.string.book_status_catalog_label
        }

        MediaType.SHOW -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.show_status_consuming_label
            TrackStatusType.CONSUMED -> Res.string.show_status_consumed_label
            TrackStatusType.DROPPED -> Res.string.show_status_dropped_label
            TrackStatusType.CATALOG -> Res.string.show_status_catalog_label
        }

        MediaType.VIDEOGAME -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.videogame_status_consuming_label
            TrackStatusType.CONSUMED -> Res.string.videogame_status_consumed_label
            TrackStatusType.DROPPED -> Res.string.videogame_status_dropped_label
            TrackStatusType.CATALOG -> Res.string.videogame_status_catalog_label
        }

        MediaType.BOARDGAME -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.boardgame_status_consuming_label
            TrackStatusType.CONSUMED -> Res.string.boardgame_status_consumed_label
            TrackStatusType.DROPPED -> Res.string.boardgame_status_dropped_label
            TrackStatusType.CATALOG -> Res.string.boardgame_status_catalog_label
        }

        MediaType.ALBUM -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.album_status_consuming_label
            TrackStatusType.CONSUMED -> Res.string.album_status_consumed_label
            TrackStatusType.DROPPED -> Res.string.album_status_dropped_label
            TrackStatusType.CATALOG -> Res.string.album_status_catalog_label
        }
    }
}

@Composable
fun getSublabelForStatus(mediaType: MediaType, status: TrackStatusType): StringResource {
    return when (mediaType) {
        MediaType.MOVIE -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.movie_status_consuming_sublabel
            TrackStatusType.CONSUMED -> Res.string.movie_status_consumed_sublabel
            TrackStatusType.DROPPED -> Res.string.movie_status_dropped_sublabel
            TrackStatusType.CATALOG -> Res.string.movie_status_catalog_sublabel
        }

        MediaType.BOOK -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.book_status_consuming_sublabel
            TrackStatusType.CONSUMED -> Res.string.book_status_consumed_sublabel
            TrackStatusType.DROPPED -> Res.string.book_status_dropped_sublabel
            TrackStatusType.CATALOG -> Res.string.book_status_catalog_sublabel
        }

        MediaType.SHOW -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.show_status_consuming_sublabel
            TrackStatusType.CONSUMED -> Res.string.show_status_consumed_sublabel
            TrackStatusType.DROPPED -> Res.string.show_status_dropped_sublabel
            TrackStatusType.CATALOG -> Res.string.show_status_catalog_sublabel
        }

        MediaType.VIDEOGAME -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.videogame_status_consuming_sublabel
            TrackStatusType.CONSUMED -> Res.string.videogame_status_consumed_sublabel
            TrackStatusType.DROPPED -> Res.string.videogame_status_dropped_sublabel
            TrackStatusType.CATALOG -> Res.string.videogame_status_catalog_sublabel
        }

        MediaType.BOARDGAME -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.boardgame_status_consuming_sublabel
            TrackStatusType.CONSUMED -> Res.string.boardgame_status_consumed_sublabel
            TrackStatusType.DROPPED -> Res.string.boardgame_status_dropped_sublabel
            TrackStatusType.CATALOG -> Res.string.boardgame_status_catalog_sublabel
        }

        MediaType.ALBUM -> when (status) {
            TrackStatusType.CONSUMING -> Res.string.album_status_consuming_sublabel
            TrackStatusType.CONSUMED -> Res.string.album_status_consumed_sublabel
            TrackStatusType.DROPPED -> Res.string.album_status_dropped_sublabel
            TrackStatusType.CATALOG -> Res.string.album_status_catalog_sublabel
        }
    }
}

@Composable
fun getStatusIcon(status: TrackStatusType, isFilled: Boolean): ImageVector {
    return when (status) {
        TrackStatusType.CONSUMING -> if (isFilled) Icons.Filled.Visibility else Icons.Outlined.Visibility
        TrackStatusType.CONSUMED -> if (isFilled) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle
        TrackStatusType.DROPPED -> if (isFilled) Icons.Filled.Cancel else Icons.Outlined.Cancel
        TrackStatusType.CATALOG -> if (isFilled) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
    }
}