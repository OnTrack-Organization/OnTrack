package de.ashman.ontrack.features.tracking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
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
import ontrack.composeapp.generated.resources.no_rating
import ontrack.composeapp.generated.resources.rating_five
import ontrack.composeapp.generated.resources.rating_four
import ontrack.composeapp.generated.resources.rating_one
import ontrack.composeapp.generated.resources.rating_subtitle
import ontrack.composeapp.generated.resources.rating_three
import ontrack.composeapp.generated.resources.rating_two
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackStatus.getLabel(mediaType: MediaType): StringResource {
    return when (mediaType) {
        MediaType.MOVIE -> when (this) {
            TrackStatus.CONSUMING -> Res.string.movie_status_consuming_label
            TrackStatus.CONSUMED -> Res.string.movie_status_consumed_label
            TrackStatus.DROPPED -> Res.string.movie_status_dropped_label
            TrackStatus.CATALOG -> Res.string.movie_status_catalog_label
        }

        MediaType.BOOK -> when (this) {
            TrackStatus.CONSUMING -> Res.string.book_status_consuming_label
            TrackStatus.CONSUMED -> Res.string.book_status_consumed_label
            TrackStatus.DROPPED -> Res.string.book_status_dropped_label
            TrackStatus.CATALOG -> Res.string.book_status_catalog_label
        }

        MediaType.SHOW -> when (this) {
            TrackStatus.CONSUMING -> Res.string.show_status_consuming_label
            TrackStatus.CONSUMED -> Res.string.show_status_consumed_label
            TrackStatus.DROPPED -> Res.string.show_status_dropped_label
            TrackStatus.CATALOG -> Res.string.show_status_catalog_label
        }

        MediaType.VIDEOGAME -> when (this) {
            TrackStatus.CONSUMING -> Res.string.videogame_status_consuming_label
            TrackStatus.CONSUMED -> Res.string.videogame_status_consumed_label
            TrackStatus.DROPPED -> Res.string.videogame_status_dropped_label
            TrackStatus.CATALOG -> Res.string.videogame_status_catalog_label
        }

        MediaType.BOARDGAME -> when (this) {
            TrackStatus.CONSUMING -> Res.string.boardgame_status_consuming_label
            TrackStatus.CONSUMED -> Res.string.boardgame_status_consumed_label
            TrackStatus.DROPPED -> Res.string.boardgame_status_dropped_label
            TrackStatus.CATALOG -> Res.string.boardgame_status_catalog_label
        }

        MediaType.ALBUM -> when (this) {
            TrackStatus.CONSUMING -> Res.string.album_status_consuming_label
            TrackStatus.CONSUMED -> Res.string.album_status_consumed_label
            TrackStatus.DROPPED -> Res.string.album_status_dropped_label
            TrackStatus.CATALOG -> Res.string.album_status_catalog_label
        }
    }
}

@Composable
fun TrackStatus.getSublabel(mediaType: MediaType): StringResource {
    return when (mediaType) {
        MediaType.MOVIE -> when (this) {
            TrackStatus.CONSUMING -> Res.string.movie_status_consuming_sublabel
            TrackStatus.CONSUMED -> Res.string.movie_status_consumed_sublabel
            TrackStatus.DROPPED -> Res.string.movie_status_dropped_sublabel
            TrackStatus.CATALOG -> Res.string.movie_status_catalog_sublabel
        }

        MediaType.BOOK -> when (this) {
            TrackStatus.CONSUMING -> Res.string.book_status_consuming_sublabel
            TrackStatus.CONSUMED -> Res.string.book_status_consumed_sublabel
            TrackStatus.DROPPED -> Res.string.book_status_dropped_sublabel
            TrackStatus.CATALOG -> Res.string.book_status_catalog_sublabel
        }

        MediaType.SHOW -> when (this) {
            TrackStatus.CONSUMING -> Res.string.show_status_consuming_sublabel
            TrackStatus.CONSUMED -> Res.string.show_status_consumed_sublabel
            TrackStatus.DROPPED -> Res.string.show_status_dropped_sublabel
            TrackStatus.CATALOG -> Res.string.show_status_catalog_sublabel
        }

        MediaType.VIDEOGAME -> when (this) {
            TrackStatus.CONSUMING -> Res.string.videogame_status_consuming_sublabel
            TrackStatus.CONSUMED -> Res.string.videogame_status_consumed_sublabel
            TrackStatus.DROPPED -> Res.string.videogame_status_dropped_sublabel
            TrackStatus.CATALOG -> Res.string.videogame_status_catalog_sublabel
        }

        MediaType.BOARDGAME -> when (this) {
            TrackStatus.CONSUMING -> Res.string.boardgame_status_consuming_sublabel
            TrackStatus.CONSUMED -> Res.string.boardgame_status_consumed_sublabel
            TrackStatus.DROPPED -> Res.string.boardgame_status_dropped_sublabel
            TrackStatus.CATALOG -> Res.string.boardgame_status_catalog_sublabel
        }

        MediaType.ALBUM -> when (this) {
            TrackStatus.CONSUMING -> Res.string.album_status_consuming_sublabel
            TrackStatus.CONSUMED -> Res.string.album_status_consumed_sublabel
            TrackStatus.DROPPED -> Res.string.album_status_dropped_sublabel
            TrackStatus.CATALOG -> Res.string.album_status_catalog_sublabel
        }
    }
}

@Composable
fun TrackStatus.getIcon(isFilled: Boolean = false): ImageVector {
    return when (this) {
        TrackStatus.CONSUMING -> if (isFilled) Icons.Filled.Visibility else Icons.Outlined.Visibility
        TrackStatus.CONSUMED -> if (isFilled) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle
        TrackStatus.DROPPED -> if (isFilled) Icons.Filled.Cancel else Icons.Outlined.Cancel
        TrackStatus.CATALOG -> if (isFilled) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
    }
}

@Composable
fun TrackStatus?.getColor(): Color {
    return when (this) {
        TrackStatus.CONSUMING -> MaterialTheme.colorScheme.secondaryContainer
        TrackStatus.CONSUMED -> MaterialTheme.colorScheme.tertiaryContainer
        TrackStatus.DROPPED -> MaterialTheme.colorScheme.errorContainer
        TrackStatus.CATALOG -> MaterialTheme.colorScheme.primaryContainer
        null -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun getRatingLabel(rating: Double?, maxRating: Int): String {
    val ratingLabel = when (rating) {
        null -> stringResource(Res.string.no_rating)
        1.0 -> stringResource(Res.string.rating_one)
        2.0 -> stringResource(Res.string.rating_two)
        3.0 -> stringResource(Res.string.rating_three)
        4.0 -> stringResource(Res.string.rating_four)
        5.0 -> stringResource(Res.string.rating_five)
        else -> ""
    }

    return if (rating == null) {
        ratingLabel
    } else {
        stringResource(Res.string.rating_subtitle, rating, maxRating, ratingLabel)
    }
}
