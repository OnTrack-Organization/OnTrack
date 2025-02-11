package de.ashman.ontrack.features.feed

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.detail.components.MiniStarRatingBar
import de.ashman.ontrack.features.detail.components.formatDateTime
import de.ashman.ontrack.features.tracking.getStatusIcon
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_comments_count
import ontrack.composeapp.generated.resources.feed_likes_count
import org.jetbrains.compose.resources.pluralStringResource

@Composable
fun FeedCard(
    tracking: Tracking,
    onClickLike: () -> Unit,
    onShowComments: () -> Unit,
    onClickTrackingHistory: () -> Unit,
) {
    Card(
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
                mediaCoverUrl = tracking.mediaCoverUrl,
                reviewTitle = tracking.reviewTitle,
                reviewDescription = tracking.reviewDescription,
                reviewRating = tracking.rating,
                trackStatus = tracking.status,
            )

            FeedCardFooter(
                isLiked = tracking.isLikedByCurrentUser,
                likeCount = tracking.likeCount,
                likeImages = tracking.likeImages,
                commentCount = tracking.commentCount,
                onLikeTracking = onClickLike,
                onShowComments = onShowComments,
            )
        }
    }
}

@Composable
fun FeedCardHeader(
    userImageUrl: String?,
    username: String?,
    timestamp: String,
    onShowTrackingHistory: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserImage(userImageUrl = userImageUrl)

            Column {
                username?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
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
    mediaTitle: String?,
    mediaCoverUrl: String?,
    trackStatus: TrackStatus?,
    reviewRating: Double?,
    reviewTitle: String?,
    reviewDescription: String?,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
                    mediaTitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium.copy(
                                lineBreak = LineBreak.Simple,
                            ),
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = true,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    trackStatus?.getStatusIcon(true)?.let {
                        Icon(
                            imageVector = it, it.name,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    MiniStarRatingBar(
                        rating = reviewRating
                    )
                }
            }

            MediaPoster(
                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                coverUrl = mediaCoverUrl
            )
        }

        if (!reviewDescription.isNullOrBlank() || !reviewTitle.isNullOrBlank()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    )
                }
            }
        }
    }
}

@Composable
fun FeedCardFooter(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    likeImages: List<String>,
    onLikeTracking: () -> Unit,
    onShowComments: () -> Unit,
) {
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

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LikeImages(
                imageUrls = likeImages,
            )

            if (likeCount > 0) {
                Text(
                    text = pluralStringResource(Res.plurals.feed_likes_count, likeCount, likeCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (commentCount > 0) {
            Text(
                text = pluralStringResource(Res.plurals.feed_comments_count, commentCount, commentCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
fun LikeImages(
    imageUrls: List<String>,
) {
    if (imageUrls.isNotEmpty()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-8).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            imageUrls.forEachIndexed { index, imageUrl ->
                UserImage(
                    userImageUrl = imageUrl,
                    modifier = Modifier
                        .size(24.dp)
                        .zIndex((imageUrls.size - index).toFloat()),
                )
            }
        }
    }
}

@Composable
fun UserImage(
    userImageUrl: String?,
    modifier: Modifier = Modifier.size(42.dp),
) {
    val painter = rememberAsyncImagePainter(userImageUrl)

    Surface(
        modifier = modifier,
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
}