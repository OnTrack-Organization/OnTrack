package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import de.ashman.ontrack.util.TrackStatus
import de.ashman.ontrack.util.TrackStatusEnum
import ontrack.composeapp.generated.resources.*
import ontrack.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackBottomSheetContent(
    mediaType: MediaType,
    onStatusSelected: (TrackStatusEnum) -> Unit,
) {
    // TODO den TrackStatus hier bauen und dann weiterreichen
    TrackStatusButtons(
        mediaType = mediaType,
        onStatusSelected = onStatusSelected,
    )
}

@Composable
fun TrackStatusButtons(
    mediaType: MediaType,
    onStatusSelected: (TrackStatusEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedStatus by remember { mutableStateOf<TrackStatusEnum>(TrackStatusEnum.NONE) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrackStatusButton(
            onClick = {
                selectedStatus = TrackStatusEnum.CONSUMING
                onStatusSelected(selectedStatus)
            },
            icon = Icons.Outlined.Visibility,
            filledIcon = Icons.Filled.Visibility,
            labelRes = getConsumingLabel(mediaType),
            subLabelRes = getConsumingSublabel(mediaType),
            isSelected = selectedStatus == TrackStatusEnum.CONSUMING
        )

        TrackStatusButton(
            onClick = {
                selectedStatus = TrackStatusEnum.CONSUMED
                onStatusSelected(selectedStatus)
            },
            icon = Icons.Outlined.CheckCircle,
            filledIcon = Icons.Filled.CheckCircle,
            labelRes = getConsumedLabel(mediaType),
            subLabelRes = getConsumedSublabel(mediaType),
            isSelected = selectedStatus == TrackStatusEnum.CONSUMED
        )

        TrackStatusButton(
            onClick = {
                selectedStatus = TrackStatusEnum.DROPPED
                onStatusSelected(selectedStatus)
            },
            icon = Icons.Outlined.Cancel,
            filledIcon = Icons.Filled.Cancel,
            labelRes = getDroppedLabel(mediaType),
            subLabelRes = getDroppedSublabel(mediaType),
            isSelected = selectedStatus == TrackStatusEnum.DROPPED
        )

        TrackStatusButton(
            onClick = {
                selectedStatus = TrackStatusEnum.CATALOG
                onStatusSelected(selectedStatus)
            },
            icon = Icons.Outlined.BookmarkBorder,
            filledIcon = Icons.Filled.Bookmark,
            labelRes = getCatalogLabel(mediaType),
            subLabelRes = getCatalogSublabel(mediaType),
            isSelected = selectedStatus == TrackStatusEnum.CATALOG
        )
    }
}

@Composable
fun TrackStatusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    filledIcon: ImageVector,
    labelRes: StringResource,
    subLabelRes: StringResource,
    isSelected: Boolean,
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
                imageVector = if (isSelected) filledIcon else icon,
                contentDescription = stringResource(labelRes),
            )
            Column {
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(subLabelRes),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun getConsumingLabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_consuming_label
    MediaType.BOOK -> Res.string.book_status_consuming_label
    MediaType.SHOW -> Res.string.show_status_consuming_label
    MediaType.VIDEOGAME -> Res.string.videogame_status_consuming_label
    MediaType.BOARDGAME -> Res.string.boardgame_status_consuming_label
    MediaType.ALBUM -> Res.string.album_status_consuming_label
}

private fun getConsumingSublabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_consuming_sublabel
    MediaType.BOOK -> Res.string.book_status_consuming_sublabel
    MediaType.SHOW -> Res.string.show_status_consuming_sublabel
    MediaType.VIDEOGAME -> Res.string.videogame_status_consuming_sublabel
    MediaType.BOARDGAME -> Res.string.boardgame_status_consuming_sublabel
    MediaType.ALBUM -> Res.string.album_status_consuming_sublabel
}

private fun getConsumedLabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_consumed_label
    MediaType.BOOK -> Res.string.book_status_consumed_label
    MediaType.SHOW -> Res.string.show_status_consumed_label
    MediaType.VIDEOGAME -> Res.string.videogame_status_consumed_label
    MediaType.BOARDGAME -> Res.string.boardgame_status_consumed_label
    MediaType.ALBUM -> Res.string.album_status_consumed_label
}

private fun getConsumedSublabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_consumed_sublabel
    MediaType.BOOK -> Res.string.book_status_consumed_sublabel
    MediaType.SHOW -> Res.string.show_status_consumed_sublabel
    MediaType.VIDEOGAME -> Res.string.videogame_status_consumed_sublabel
    MediaType.BOARDGAME -> Res.string.boardgame_status_consumed_sublabel
    MediaType.ALBUM -> Res.string.album_status_consumed_sublabel
}

private fun getDroppedLabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_dropped_label
    MediaType.BOOK -> Res.string.book_status_dropped_label
    MediaType.SHOW -> Res.string.show_status_dropped_label
    MediaType.VIDEOGAME -> Res.string.videogame_status_dropped_label
    MediaType.BOARDGAME -> Res.string.boardgame_status_dropped_label
    MediaType.ALBUM -> Res.string.album_status_dropped_label
}

private fun getDroppedSublabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_dropped_sublabel
    MediaType.BOOK -> Res.string.book_status_dropped_sublabel
    MediaType.SHOW -> Res.string.show_status_dropped_sublabel
    MediaType.VIDEOGAME -> Res.string.videogame_status_dropped_sublabel
    MediaType.BOARDGAME -> Res.string.boardgame_status_dropped_sublabel
    MediaType.ALBUM -> Res.string.album_status_dropped_sublabel
}

private fun getCatalogLabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_catalog_label
    MediaType.BOOK -> Res.string.book_status_catalog_label
    MediaType.SHOW -> Res.string.show_status_catalog_label
    MediaType.VIDEOGAME -> Res.string.videogame_status_catalog_label
    MediaType.BOARDGAME -> Res.string.boardgame_status_catalog_label
    MediaType.ALBUM -> Res.string.album_status_catalog_label
}

private fun getCatalogSublabel(mediaType: MediaType): StringResource = when (mediaType) {
    MediaType.MOVIE -> Res.string.movie_status_catalog_sublabel
    MediaType.BOOK -> Res.string.book_status_catalog_sublabel
    MediaType.SHOW -> Res.string.show_status_catalog_sublabel
    MediaType.VIDEOGAME -> Res.string.videogame_status_catalog_sublabel
    MediaType.BOARDGAME -> Res.string.boardgame_status_catalog_sublabel
    MediaType.ALBUM -> Res.string.album_status_catalog_sublabel
}