package de.ashman.ontrack.features.detail.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
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
import de.ashman.ontrack.features.common.MiniStarRatingBar
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.formatDateTime
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.share.ShareCardHeader
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
    hasTracking: Boolean,
    onUserClick: (String) -> Unit,
    onAddToCatalogClick: () -> Unit,
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
                        onClickUser = { onUserClick(it.userId) },
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
                    SimpleFriendTrackingCard(
                        userImageUrl = it.userImageUrl,
                        username = it.username,
                        timestamp = it.timestamp,
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
                    enabled = !hasTracking,
                    onClick = onAddToCatalogClick,
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(
    userImageUrl: String?,
    username: String?,
    timestamp: String,
    message: String?,
    onClickUser: () -> Unit,
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
                onClick = onClickUser
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

        if (!message.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(start = 56.dp),
                text = message,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun SimpleFriendTrackingCard(
    userImageUrl: String?,
    username: String,
    timestamp: Long,
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
        ShareCardHeader(
            userImageUrl = userImageUrl,
            username = username,
            timestamp = timestamp,
            mediaType = mediaType,
            trackStatus = trackStatus,
            onClickUser = onUserClick,
        )

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