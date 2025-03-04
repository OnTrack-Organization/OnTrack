package de.ashman.ontrack.features.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.MiniStarRatingBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.common.TrackingCardHeader
import de.ashman.ontrack.features.common.contentSizeAnimation
import de.ashman.ontrack.features.common.formatDateTime
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_comments_count
import ontrack.composeapp.generated.resources.feed_likes_count
import org.jetbrains.compose.resources.pluralStringResource

@Composable
fun FeedCard(
    tracking: Tracking,
    onLike: () -> Unit,
    onShowComments: () -> Unit,
    onShowLikes: () -> Unit,
    onClickCover: (MediaNavigationItems) -> Unit,
    onClickUser: () -> Unit,
) {
    Card(
        modifier = Modifier.contentSizeAnimation(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    TrackingCardHeader(
                        userImageUrl = tracking.userImageUrl,
                        username = tracking.username,
                        timestamp = tracking.timestamp.formatDateTime(),
                        mediaType = tracking.mediaType,
                        trackStatus = tracking.status!!,
                        onUserClick = onClickUser,
                    )

                    FeedCardMediaTitle(
                        mediaType = tracking.mediaType,
                        mediaTitle = tracking.mediaTitle,
                    )
                }

                MediaPoster(
                    modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                    coverUrl = tracking.mediaCoverUrl,
                    onClick = {
                        onClickCover(
                            MediaNavigationItems(
                                id = tracking.mediaId,
                                mediaType = tracking.mediaType,
                                title = tracking.mediaTitle,
                                coverUrl = tracking.mediaCoverUrl,
                            )
                        )
                    },
                )
            }

            FeedCardReview(
                reviewTitle = tracking.reviewTitle,
                reviewDescription = tracking.reviewDescription,
                reviewRating = tracking.rating,
                starColor = contentColorFor(tracking.status.getColor()),
            )

            FeedCardFooter(
                isLiked = tracking.isLikedByCurrentUser,
                likeCount = tracking.likeCount,
                likeImages = tracking.likeImages,
                commentCount = tracking.commentCount,
                onLikeTracking = onLike,
                onShowComments = onShowComments,
                onShowLikes = onShowLikes,
            )
        }
    }
}

@Composable
fun FeedCardMediaTitle(
    mediaType: MediaType,
    mediaTitle: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = mediaType.getMediaTypeUi().outlinedIcon,
            contentDescription = null,
        )
        Text(
            text = mediaTitle,
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

@Composable
fun FeedCardReview(
    reviewTitle: String?,
    reviewDescription: String?,
    reviewRating: Double?,
    starColor: Color,
) {
    var expanded by remember { mutableStateOf(false) }

    reviewRating?.let {
        MiniStarRatingBar(
            rating = reviewRating,
            starColor = starColor,
        )
    }

    if (!reviewDescription.isNullOrBlank() || !reviewTitle.isNullOrBlank()) {
        Column(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { expanded = !expanded }
                ),
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

@Composable
fun FeedCardFooter(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    likeImages: List<String>,
    onLikeTracking: () -> Unit,
    onShowComments: () -> Unit,
    onShowLikes: () -> Unit,
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

        AnimatedVisibility(
            visible = likeImages.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
        ) {
            Box(
                modifier = Modifier.clickable {
                    onShowLikes()
                },
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-8).dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        likeImages.forEachIndexed { index, imageUrl ->
                            PersonImage(
                                userImageUrl = imageUrl,
                                onClick = onShowLikes,
                                modifier = Modifier
                                    .size(24.dp)
                                    .zIndex((likeImages.size - index).toFloat()),
                            )
                        }
                    }

                    if (likeCount > 0) {
                        Text(
                            text = pluralStringResource(Res.plurals.feed_likes_count, likeCount, likeCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
}
