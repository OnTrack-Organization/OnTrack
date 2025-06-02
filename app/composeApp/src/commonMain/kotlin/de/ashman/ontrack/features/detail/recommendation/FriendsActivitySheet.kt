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
import de.ashman.ontrack.domain.recommendation.FriendsActivity
import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.MiniStarRatingBar
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.notification.formatTimeAgoString
import de.ashman.ontrack.features.share.ShareCardHeader
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.add_to_catalog_button
import ontrack.composeapp.generated.resources.friends_activity_empty
import ontrack.composeapp.generated.resources.friends_activity_title
import ontrack.composeapp.generated.resources.recommendations_title
import ontrack.composeapp.generated.resources.trackings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsActivitySheet(
    friendsActivity: FriendsActivity?,
    isMediaUntracked: Boolean,
    mediaType: MediaType,
    isAddingToCatalog: Boolean,
    onUserClick: (String) -> Unit,
    onCatalogRecommendation: () -> Unit,
) {
    if (friendsActivity == null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.friends_activity_title),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.friends_activity_empty),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (friendsActivity.recommendations.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.recommendations_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(friendsActivity.recommendations) {
                        RecommendationCard(
                            profilePictureUrl = it.user.profilePictureUrl,
                            username = it.user.username,
                            timestamp = it.timestamp,
                            message = it.message,
                            onClickUser = { onUserClick(it.user.id) },
                        )
                    }
                }
            }

            if (friendsActivity.trackings.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.trackings_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(friendsActivity.trackings) {
                        SimpleTrackingCard(
                            profilePictureUrl = it.user.profilePictureUrl,
                            username = it.user.username,
                            timestamp = it.timestamp,
                            mediaType = mediaType,
                            trackStatus = it.status,
                            review = it.review,
                            onUserClick = { onUserClick(it.user.id) },
                        )
                    }
                }
            }

            if (friendsActivity.recommendations.isNotEmpty()) {
                OnTrackButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = Res.string.add_to_catalog_button,
                    icon = Icons.Default.Bookmark,
                    enabled = isMediaUntracked,
                    isLoading = isAddingToCatalog,
                    onClick = onCatalogRecommendation,
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(
    profilePictureUrl: String?,
    username: String,
    timestamp: Long,
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
                profilePictureUrl = profilePictureUrl,
                onClick = onClickUser
            )

            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = timestamp.formatTimeAgoString(),
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
fun SimpleTrackingCard(
    profilePictureUrl: String?,
    username: String,
    timestamp: Long,
    mediaType: MediaType,
    trackStatus: TrackStatus,
    review: Review?,
    onUserClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShareCardHeader(
            profilePictureUrl = profilePictureUrl,
            username = username,
            timestamp = timestamp,
            mediaType = mediaType,
            trackStatus = trackStatus,
            onClickUser = onUserClick,
        )

        review?.let {
            Column(modifier = Modifier.padding(start = 56.dp)) {
                MiniStarRatingBar(
                    rating = it.rating,
                    starColor = contentColorFor(trackStatus.getColor()),
                )
            }

            Column(modifier = Modifier.padding(start = 56.dp)) {
                it.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                it.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}