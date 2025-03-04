package de.ashman.ontrack.features.detail.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.DoNotDisturb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedIconButton
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.formatDateTime
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.detail.components.MiniStarRatingBar
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_friends_activity_empty
import ontrack.composeapp.generated.resources.detail_friends_activity_title
import ontrack.composeapp.generated.resources.recommendations_add_to_catalog_button
import ontrack.composeapp.generated.resources.recommendations_title
import ontrack.composeapp.generated.resources.trackings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsActivitySheet(
    recommendations: List<Recommendation>,
    // TODO maybe new FriendTracking domain
    friendTrackings: List<Tracking>,
    onUserClick: (String) -> Unit,
    onAddToCatalogClick: () -> Unit,
    onPassClick: () -> Unit,
) {
    val buttonsVisible = recommendations.isNotEmpty()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (recommendations.isEmpty() && friendTrackings.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.detail_friends_activity_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.detail_friends_activity_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            return@Column
        }

        if (recommendations.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.recommendations_title),
                style = MaterialTheme.typography.titleMedium,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(recommendations) {
                    RecommendationCard(
                        userImageUrl = it.userImageUrl,
                        username = it.username,
                        timestamp = it.timestamp.formatDateTime(),
                        message = it.message,
                        onUserClick = { onUserClick(it.userId) },
                    )
                }
            }
        }

        if (friendTrackings.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.trackings_title),
                style = MaterialTheme.typography.titleMedium,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(friendTrackings) {
                    TrackingCard(
                        userImageUrl = it.userImageUrl,
                        username = it.username,
                        timestamp = it.timestamp.formatDateTime(),
                        mediaType = it.mediaType,
                        trackStatus = it.status!!,
                        rating = it.rating,
                        reviewTitle = it.reviewTitle,
                        reviewDescription = it.reviewDescription,
                        onUserClick = { onUserClick(it.userId) },
                    )
                }
            }
        }

        if (buttonsVisible) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OnTrackButton(
                    modifier = Modifier.weight(1f),
                    text = Res.string.recommendations_add_to_catalog_button,
                    icon = Icons.Default.Bookmark,
                    onClick = onAddToCatalogClick,
                )

                OnTrackOutlinedIconButton(
                    icon = Icons.Outlined.DoNotDisturb,
                    color = TrackStatus.CATALOG.getColor(),
                    onClick = onPassClick,
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    userImageUrl: String?,
    username: String?,
    timestamp: String,
    message: String?,
    onUserClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PersonImage(
                userImageUrl = userImageUrl,
                onClick = onUserClick
            )

            Column {
                username?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun TrackingCard(
    userImageUrl: String?,
    username: String?,
    timestamp: String,
    mediaType: MediaType,
    trackStatus: TrackStatus,
    rating: Double?,
    reviewTitle: String?,
    reviewDescription: String?,
    onUserClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PersonImage(
                userImageUrl = userImageUrl,
                onClick = onUserClick
            )

            Column {
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

        Column(modifier = Modifier.padding(start = 56.dp)) {
            rating?.let {
                MiniStarRatingBar(
                    rating = it,
                    starColor = contentColorFor(trackStatus.getColor()),
                )
            }
        }

        Column(modifier = Modifier.padding(start = 56.dp)) {
            reviewTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            reviewDescription?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}