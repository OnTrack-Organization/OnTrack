package de.ashman.ontrack.features.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.Tracking
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.detail.components.ReviewCardContent
import de.ashman.ontrack.features.detail.components.formatDateTime
import de.ashman.ontrack.util.getMediaTypeUi

@Composable
fun FeedCard(
    tracking: Tracking,
    isLiked: Boolean,
    onClickLike: () -> Unit,
    onShowComments: () -> Unit,
    onClickTrackingHistory: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.clickable(
            onClick = { expanded = !expanded },
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FeedCardHeader(
                userImageUrl = tracking.userImageUrl,
                username = tracking.username,
                timestamp = tracking.timestamp.formatDateTime(),
                onShowTrackingHistory = onClickTrackingHistory,
            )

            FeedCardContent(
                mediaType = tracking.mediaType,
                mediaTitle = tracking.mediaTitle,
                mediaCoverUrl = tracking.mediaImageUrl,
                reviewTitle = tracking.reviewTitle,
                reviewDescription = tracking.reviewDescription,
                reviewRating = tracking.rating,
                trackStatus = tracking.status,
                expanded = expanded,
            )

            FeedCardFooter(
                isLiked = isLiked,
                onLikeTracking = onClickLike,
                onShowComments = onShowComments,
            )
        }
    }
}

@Composable
fun FeedCardHeader(
    userImageUrl: String,
    username: String,
    timestamp: String,
    onShowTrackingHistory: () -> Unit,
) {
    val painter = rememberAsyncImagePainter(userImageUrl)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                val state = painter.state.collectAsState().value

                when (state) {
                    is AsyncImagePainter.State.Empty -> {}
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = painter,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Account Image",
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            imageVector = Icons.Default.Person,
                            contentDescription = "No Image",
                        )
                    }
                }
            }
            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(
            onClick = onShowTrackingHistory
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun FeedCardContent(
    mediaType: MediaType,
    mediaTitle: String,
    mediaCoverUrl: String?,
    trackStatus: TrackStatus?,
    reviewRating: Int?,
    reviewTitle: String?,
    reviewDescription: String?,
    expanded: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = mediaType.getMediaTypeUi().icon,
                    contentDescription = null,
                )
                Text(
                    text = mediaTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            ReviewCardContent(
                reviewTitle = reviewTitle,
                reviewDescription = reviewDescription,
                reviewRating = reviewRating,
                trackStatus = trackStatus,
                expanded = expanded,
            )
        }

        MediaPoster(
            modifier = Modifier.height(SMALL_POSTER_HEIGHT),
            coverUrl = mediaCoverUrl
        )
    }
}

@Composable
fun FeedCardFooter(
    isLiked: Boolean,
    onLikeTracking: () -> Unit,
    onShowComments: () -> Unit,
) {
    // TODO add like counter, image urls and texts
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onLikeTracking
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
            )
        }
        IconButton(
            onClick = onShowComments
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Chat,
                contentDescription = null,
            )
        }
    }
}