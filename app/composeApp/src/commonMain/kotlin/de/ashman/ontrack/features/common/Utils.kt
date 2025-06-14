package de.ashman.ontrack.features.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
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
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.network.services.account.UsernameError
import de.ashman.ontrack.network.services.report.dto.ReportReason
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
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
import ontrack.composeapp.generated.resources.report_reason_false_information
import ontrack.composeapp.generated.resources.report_reason_harassment
import ontrack.composeapp.generated.resources.report_reason_hate_speech
import ontrack.composeapp.generated.resources.report_reason_inappropriate_content
import ontrack.composeapp.generated.resources.report_reason_other
import ontrack.composeapp.generated.resources.report_reason_spam
import ontrack.composeapp.generated.resources.report_reason_spoiler
import ontrack.composeapp.generated.resources.settings_username_empty
import ontrack.composeapp.generated.resources.settings_username_invalid_characters
import ontrack.composeapp.generated.resources.settings_username_taken
import ontrack.composeapp.generated.resources.settings_username_too_long
import ontrack.composeapp.generated.resources.settings_username_too_short
import ontrack.composeapp.generated.resources.settings_username_uppercase
import ontrack.composeapp.generated.resources.settings_username_whitespace
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

// Default Poster Size ist 2:3 Ratio
val DEFAULT_POSTER_HEIGHT = 224.dp
val SMALL_POSTER_HEIGHT = 112.dp
val MINI_POSTER_HEIGHT = 80.dp

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

fun Modifier.contentSizeAnimation(): Modifier =
    this then Modifier.animateContentSize(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

fun UsernameError.getLabel(): StringResource {
    return when (this) {
        UsernameError.EMPTY -> Res.string.settings_username_empty
        UsernameError.WHITESPACE -> Res.string.settings_username_whitespace
        UsernameError.TAKEN -> Res.string.settings_username_taken
        UsernameError.TOO_LONG -> Res.string.settings_username_too_long
        UsernameError.TOO_SHORT -> Res.string.settings_username_too_short
        UsernameError.INVALID_CHARACTERS -> Res.string.settings_username_invalid_characters
        UsernameError.NO_UPPERCASE -> Res.string.settings_username_uppercase
    }
}

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
        TrackStatus.CATALOG -> if (isFilled) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
        TrackStatus.CONSUMING -> if (isFilled) Icons.Filled.Visibility else Icons.Outlined.Visibility
        TrackStatus.CONSUMED -> if (isFilled) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle
        TrackStatus.DROPPED -> if (isFilled) Icons.Filled.Cancel else Icons.Outlined.Cancel
    }
}

@Composable
fun TrackStatus?.getColor(): Color {
    return when (this) {
        TrackStatus.CATALOG -> MaterialTheme.colorScheme.primaryContainer
        TrackStatus.CONSUMING -> MaterialTheme.colorScheme.secondaryContainer
        TrackStatus.CONSUMED -> MaterialTheme.colorScheme.tertiaryContainer
        TrackStatus.DROPPED -> MaterialTheme.colorScheme.errorContainer
        null -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun getRatingLabel(rating: Double?, maxRating: Int): String {
    val ratingLabel = when (rating) {
        null -> stringResource(Res.string.no_rating)
        in 0.5..1.0 -> stringResource(Res.string.rating_one)
        in 1.5..2.0 -> stringResource(Res.string.rating_two)
        in 2.5..3.0 -> stringResource(Res.string.rating_three)
        in 3.5..4.0 -> stringResource(Res.string.rating_four)
        in 4.5..5.0 -> stringResource(Res.string.rating_five)
        else -> ""
    }

    return if (rating == null) {
        ratingLabel
    } else {
        stringResource(
            Res.string.rating_subtitle,
            formatOneDecimal(rating),
            maxRating,
            ratingLabel
        )
    }
}

fun formatOneDecimal(rating: Double): String {
    val whole = rating.toInt()
    val decimal = ((rating - whole) * 10).toInt()
    return if (decimal == 0) {
        whole.toString()
    } else {
        "$whole.$decimal"
    }
}

fun Long.formatDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(currentSystemDefault())

    val date = "${dateTime.dayOfMonth.toString().padStart(2, '0')}.${dateTime.monthNumber.toString().padStart(2, '0')}.${dateTime.year}"
    val time = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"

    return "$date, $time"
}

fun Long.formatDayMonth(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(currentSystemDefault())

    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.monthNumber.toString().padStart(2, '0')

    return "$day.$month"
}

fun ReportReason.getLabel(): StringResource {
    return when (this) {
        ReportReason.INAPPROPRIATE_CONTENT -> Res.string.report_reason_inappropriate_content
        ReportReason.SPAM -> Res.string.report_reason_spam
        ReportReason.HARASSMENT -> Res.string.report_reason_harassment
        ReportReason.FALSE_INFORMATION -> Res.string.report_reason_false_information
        ReportReason.HATE_SPEECH -> Res.string.report_reason_hate_speech
        ReportReason.SPOILER -> Res.string.report_reason_spoiler
        ReportReason.OTHER -> Res.string.report_reason_other
    }
}

