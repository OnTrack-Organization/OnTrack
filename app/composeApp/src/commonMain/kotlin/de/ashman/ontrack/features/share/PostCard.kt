package de.ashman.ontrack.features.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.MiniStarRatingBar
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.notification.formatTimeAgoString
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.share_comments_count
import ontrack.composeapp.generated.resources.share_likes_count
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onShowComments: () -> Unit,
    onShowLikes: () -> Unit,
    onClickCover: (MediaNavigationParam) -> Unit,
    onClickUser: () -> Unit,
    onClickCard: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable { onClickCard() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                ShareCardHeader(
                    profilePictureUrl = post.user.profilePictureUrl,
                    username = post.user.username,
                    timestamp = post.tracking.timestamp,
                    mediaType = post.tracking.media.type,
                    trackStatus = post.tracking.status,
                    onClickUser = onClickUser,
                )

                ShareCardMediaTitle(
                    mediaType = post.tracking.media.type,
                    mediaTitle = post.tracking.media.title,
                )
            }

            MediaPoster(
                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                coverUrl = post.tracking.media.coverUrl,
                onClick = {
                    onClickCover(
                        MediaNavigationParam(
                            id = post.tracking.media.id,
                            type = post.tracking.media.type,
                            title = post.tracking.media.title,
                            coverUrl = post.tracking.media.coverUrl,
                        )
                    )
                },
            )
        }

        ShareCardReview(
            review = post.review,
            starColor = contentColorFor(post.tracking.status.getColor()),
        )

        ShareCardFooter(
            isLiked = post.likedByCurrentUser,
            likesPreview = post.likes,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            onLikeTracking = onLike,
            onShowComments = onShowComments,
            onShowLikes = onShowLikes,
        )
    }
}

@Composable
fun ShareCardHeader(
    profilePictureUrl: String?,
    username: String,
    timestamp: Long,
    mediaType: MediaType,
    trackStatus: TrackStatus,
    onClickUser: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PersonImage(
            profilePictureUrl = profilePictureUrl,
            onClick = onClickUser
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )

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
                    text = "${stringResource(trackStatus.getLabel(mediaType))} - ${timestamp.formatTimeAgoString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun ShareCardMediaTitle(
    mediaType: MediaType,
    mediaTitle: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = mediaType.getMediaTypeUi().outlinedIcon,
            contentDescription = null,
        )
        Text(
            text = mediaTitle,
            style = MaterialTheme.typography.bodyLarge.copy(
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
fun ShareCardReview(
    review: Review?,
    starColor: Color,
) {
    review?.let {
        MiniStarRatingBar(
            rating = it.rating,
            starColor = starColor,
        )

        if (!it.description.isNullOrBlank() || !it.title.isNullOrBlank()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (!it.title.isNullOrBlank()) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (!it.description.isNullOrBlank()) {
                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
fun ShareCardFooter(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    likesPreview: List<Like>,
    onLikeTracking: () -> Unit,
    onShowComments: () -> Unit,
    onShowLikes: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onLikeTracking) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                )
            }

            AnimatedVisibility(
                visible = likesPreview.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)),
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onShowLikes() }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-8).dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        likesPreview.forEachIndexed { index, like ->
                            PersonImage(
                                profilePictureUrl = like.user.profilePictureUrl,
                                onClick = onShowLikes,
                                modifier = Modifier
                                    .zIndex((likesPreview.size - index).toFloat()),
                                size = 24.dp,
                            )
                        }
                    }

                    if (likeCount > 0) {
                        Text(
                            text = pluralStringResource(Res.plurals.share_likes_count, likeCount, likeCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .clickable { onShowComments() }
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (commentCount > 0) {
                Text(
                    text = pluralStringResource(Res.plurals.share_comments_count, commentCount, commentCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Chat,
                contentDescription = null,
            )
        }
    }
}
