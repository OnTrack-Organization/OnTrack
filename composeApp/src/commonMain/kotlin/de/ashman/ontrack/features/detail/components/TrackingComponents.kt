package de.ashman.ontrack.features.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.tracking.MAX_RATING
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.contentSizeAnimation
import de.ashman.ontrack.features.detail.tracking.getIcon
import de.ashman.ontrack.features.feed.FeedCardHeader
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime

@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    tracking: Tracking,
    onClickTrackingHistory: () -> Unit,
    onUserClick: () -> Unit,
) {
    Column(
        modifier = modifier.contentSizeAnimation(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FeedCardHeader(
            userImageUrl = tracking.userImageUrl,
            username = tracking.username,
            timestamp = tracking.timestamp.formatDateTime(),
            onShowTrackingHistory = onClickTrackingHistory,
            onUserClick = onUserClick,
        )
        ReviewCardContent(
            reviewTitle = tracking.reviewTitle,
            reviewDescription = tracking.reviewDescription,
            reviewRating = tracking.rating,
            trackStatus = tracking.status,
        )
    }
}

@Composable
fun ReviewCardContent(
    modifier: Modifier = Modifier,
    reviewTitle: String?,
    reviewDescription: String?,
    reviewRating: Double?,
    trackStatus: TrackStatus?,
) {
    var expanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            trackStatus?.getIcon(true)?.let {
                Icon(
                    imageVector = it, it.name,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            MiniStarRatingBar(
                rating = reviewRating
            )
        }

        Column(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { expanded = !expanded }
            )
        ) {
            if (!reviewTitle.isNullOrBlank()) {
                Text(
                    text = reviewTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (!reviewDescription.isNullOrBlank()) {
                Text(
                    text = reviewDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = {
                        if (!expanded) hasOverflow = it.hasVisualOverflow
                    }
                )
            }
        }
    }
}

@Composable
fun MiniStarRatingBar(
    modifier: Modifier = Modifier,
    rating: Double?,
) {
    Row(
        modifier = modifier
    ) {
        for (i in 1..MAX_RATING) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (rating != null && i <= rating) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

fun Long.formatDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(currentSystemDefault())

    val date = "${dateTime.dayOfMonth.toString().padStart(2, '0')}.${dateTime.monthNumber.toString().padStart(2, '0')}.${dateTime.year}"
    val time = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"

    return "$date, $time"
}