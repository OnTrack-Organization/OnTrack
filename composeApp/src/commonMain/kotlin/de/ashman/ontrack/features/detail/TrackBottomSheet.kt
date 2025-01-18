package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatusEnum
import de.ashman.ontrack.util.OnTrackButton
import ontrack.composeapp.generated.resources.*
import ontrack.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackBottomSheetContent(
    mediaType: MediaType,
    onSaveTrackStatus: (TrackStatusEnum, String) -> Unit,
) {
    var currentContent by remember { mutableStateOf(TrackBottomSheetContent.TRACK_STATUS) }
    var selectedStatus by remember { mutableStateOf<TrackStatusEnum?>(null) }
    var reviewText by remember { mutableStateOf("") }

    when (currentContent) {
        TrackBottomSheetContent.TRACK_STATUS -> TrackStatusContent(
            mediaType = mediaType,
            selectedStatus = selectedStatus,
            onStatusSelected = { selectedStatus = it },
            onContinue = { currentContent = TrackBottomSheetContent.REVIEW },
        )

        TrackBottomSheetContent.REVIEW -> ReviewContent(
            reviewText = reviewText,
            onReviewChange = { reviewText = it },
            onSave = {
                selectedStatus?.let { onSaveTrackStatus(it, reviewText) }
            },
        )
    }
}

@Composable
fun TrackStatusContent(
    mediaType: MediaType,
    selectedStatus: TrackStatusEnum?,
    onStatusSelected: (TrackStatusEnum) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrackStatusEnum.entries.forEach { status ->
            val isSelected = selectedStatus == status

            TrackStatusButton(
                onClick = { onStatusSelected(status) },
                icon = getStatusIcon(status, isSelected),
                label = getLabelForStatus(mediaType, status),
                subLabel = getSublabelForStatus(mediaType, status),
                isSelected = selectedStatus == status,
            )
        }

        OnTrackButton(
            text = Res.string.continue_button,
            onClick = onContinue,
        )
    }
}

@Composable
fun TrackStatusButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: StringResource,
    subLabel: StringResource,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        ),
        onClick = { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = icon,
                contentDescription = stringResource(label),
            )
            Column {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(subLabel),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun ReviewContent(
    reviewText: String,
    onReviewChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = reviewText,
            onValueChange = onReviewChange,
            label = { Text(stringResource(Res.string.review_label)) },
        )
        OnTrackButton(
            text = Res.string.save_button,
            icon = Icons.Default.Save,
            onClick = onSave,
        )
    }
}

@Composable
fun getLabelForStatus(mediaType: MediaType, status: TrackStatusEnum): StringResource {
    return when (mediaType) {
        MediaType.MOVIE -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.movie_status_consuming_label
            TrackStatusEnum.CONSUMED -> Res.string.movie_status_consumed_label
            TrackStatusEnum.DROPPED -> Res.string.movie_status_dropped_label
            TrackStatusEnum.CATALOG -> Res.string.movie_status_catalog_label
        }

        MediaType.BOOK -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.book_status_consuming_label
            TrackStatusEnum.CONSUMED -> Res.string.book_status_consumed_label
            TrackStatusEnum.DROPPED -> Res.string.book_status_dropped_label
            TrackStatusEnum.CATALOG -> Res.string.book_status_catalog_label
        }

        MediaType.SHOW -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.show_status_consuming_label
            TrackStatusEnum.CONSUMED -> Res.string.show_status_consumed_label
            TrackStatusEnum.DROPPED -> Res.string.show_status_dropped_label
            TrackStatusEnum.CATALOG -> Res.string.show_status_catalog_label
        }

        MediaType.VIDEOGAME -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.videogame_status_consuming_label
            TrackStatusEnum.CONSUMED -> Res.string.videogame_status_consumed_label
            TrackStatusEnum.DROPPED -> Res.string.videogame_status_dropped_label
            TrackStatusEnum.CATALOG -> Res.string.videogame_status_catalog_label
        }

        MediaType.BOARDGAME -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.boardgame_status_consuming_label
            TrackStatusEnum.CONSUMED -> Res.string.boardgame_status_consumed_label
            TrackStatusEnum.DROPPED -> Res.string.boardgame_status_dropped_label
            TrackStatusEnum.CATALOG -> Res.string.boardgame_status_catalog_label
        }

        MediaType.ALBUM -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.album_status_consuming_label
            TrackStatusEnum.CONSUMED -> Res.string.album_status_consumed_label
            TrackStatusEnum.DROPPED -> Res.string.album_status_dropped_label
            TrackStatusEnum.CATALOG -> Res.string.album_status_catalog_label
        }
    }
}

@Composable
fun getSublabelForStatus(mediaType: MediaType, status: TrackStatusEnum): StringResource {
    return when (mediaType) {
        MediaType.MOVIE -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.movie_status_consuming_sublabel
            TrackStatusEnum.CONSUMED -> Res.string.movie_status_consumed_sublabel
            TrackStatusEnum.DROPPED -> Res.string.movie_status_dropped_sublabel
            TrackStatusEnum.CATALOG -> Res.string.movie_status_catalog_sublabel
        }

        MediaType.BOOK -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.book_status_consuming_sublabel
            TrackStatusEnum.CONSUMED -> Res.string.book_status_consumed_sublabel
            TrackStatusEnum.DROPPED -> Res.string.book_status_dropped_sublabel
            TrackStatusEnum.CATALOG -> Res.string.book_status_catalog_sublabel
        }

        MediaType.SHOW -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.show_status_consuming_sublabel
            TrackStatusEnum.CONSUMED -> Res.string.show_status_consumed_sublabel
            TrackStatusEnum.DROPPED -> Res.string.show_status_dropped_sublabel
            TrackStatusEnum.CATALOG -> Res.string.show_status_catalog_sublabel
        }

        MediaType.VIDEOGAME -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.videogame_status_consuming_sublabel
            TrackStatusEnum.CONSUMED -> Res.string.videogame_status_consumed_sublabel
            TrackStatusEnum.DROPPED -> Res.string.videogame_status_dropped_sublabel
            TrackStatusEnum.CATALOG -> Res.string.videogame_status_catalog_sublabel
        }

        MediaType.BOARDGAME -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.boardgame_status_consuming_sublabel
            TrackStatusEnum.CONSUMED -> Res.string.boardgame_status_consumed_sublabel
            TrackStatusEnum.DROPPED -> Res.string.boardgame_status_dropped_sublabel
            TrackStatusEnum.CATALOG -> Res.string.boardgame_status_catalog_sublabel
        }

        MediaType.ALBUM -> when (status) {
            TrackStatusEnum.CONSUMING -> Res.string.album_status_consuming_sublabel
            TrackStatusEnum.CONSUMED -> Res.string.album_status_consumed_sublabel
            TrackStatusEnum.DROPPED -> Res.string.album_status_dropped_sublabel
            TrackStatusEnum.CATALOG -> Res.string.album_status_catalog_sublabel
        }
    }
}

@Composable
fun getStatusIcon(status: TrackStatusEnum, isFilled: Boolean): ImageVector {
    return when (status) {
        TrackStatusEnum.CONSUMING -> if (isFilled) Icons.Filled.Visibility else Icons.Outlined.Visibility
        TrackStatusEnum.CONSUMED -> if (isFilled) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle
        TrackStatusEnum.DROPPED -> if (isFilled) Icons.Filled.Cancel else Icons.Outlined.Cancel
        TrackStatusEnum.CATALOG -> if (isFilled) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
    }
}

enum class TrackBottomSheetContent {
    TRACK_STATUS,
    REVIEW,
}