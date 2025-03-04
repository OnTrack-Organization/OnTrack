package de.ashman.ontrack.features.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.MAX_RATING
import de.ashman.ontrack.domain.tracking.TrackStatus
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackingCardHeader(
    userImageUrl: String?,
    username: String?,
    timestamp: String,
    mediaType: MediaType,
    trackStatus: TrackStatus,
    onUserClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PersonImage(
            userImageUrl = userImageUrl,
            onClick = onUserClick
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            username?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = trackStatus.getIcon(true), trackStatus.name,
                    tint = contentColorFor(trackStatus.getColor()),
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "${stringResource(trackStatus.getLabel(mediaType))} - $timestamp",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun MiniStarRatingBar(
    modifier: Modifier = Modifier,
    rating: Double?,
    starColor: Color,
) {
    Row(
        modifier = modifier
    ) {
        for (i in 1..MAX_RATING) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (rating != null && i <= rating) {
                    starColor
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}
