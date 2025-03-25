package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.notifications.NotificationCard
import de.ashman.ontrack.features.notifications.NotificationData
import de.ashman.ontrack.features.notifications.NotificationType
import de.ashman.ontrack.features.notifications.NotificationsScreen
import de.ashman.ontrack.features.notifications.NotificationsViewModel
import de.ashman.ontrack.theme.OnTrackTheme

@OnTrackPreview
@Composable
fun LikeNotificationCardPreview() {
    OnTrackTheme {
        NotificationCard(
            notification = NotificationData(
                type = NotificationType.LIKE,
                senderUserId = previewUser.id,
                senderUserName = previewUser.name,
                senderUserImageUrl = previewUser.imageUrl,
                repliedToUserName = null,
                trackingId = "1",
                mediaId = previewMedia.id,
                mediaType = previewMedia.mediaType,
                mediaTitle = previewMedia.title,
                mediaImageUrl = previewMedia.coverUrl,
                timestamp = previewTimestampNow,
            ),
            onNotificationClick = {},
            onUserClick = {},
            onMediaClick = {},
        )
    }
}

@OnTrackPreview
@Composable
fun CommentNotificationCardPreview() {
    OnTrackTheme {
        NotificationCard(
            notification = NotificationData(
                type = NotificationType.COMMENT,
                senderUserId = previewUser.id,
                senderUserName = previewUser.name,
                senderUserImageUrl = previewUser.imageUrl,
                repliedToUserName = null,
                trackingId = "1",
                mediaId = previewMedia.id,
                mediaType = previewMedia.mediaType,
                mediaTitle = previewMedia.title,
                mediaImageUrl = previewMedia.coverUrl,
                timestamp = previewTimestampHourAgo,
            ),
            onNotificationClick = {},
            onUserClick = {},
            onMediaClick = {},
        )
    }
}

@OnTrackPreview
@Composable
fun ReplyNotificationCardPreview() {
    OnTrackTheme {
        NotificationCard(
            notification = NotificationData(
                type = NotificationType.REPLY,
                senderUserId = previewUser.id,
                senderUserName = previewUser.name,
                senderUserImageUrl = previewUser.imageUrl,
                repliedToUserName = "Joseph Joestar",
                trackingId = "1",
                mediaId = previewMedia.id,
                mediaType = previewMedia.mediaType,
                mediaTitle = previewMedia.title,
                mediaImageUrl = previewMedia.coverUrl,
                timestamp = previewTimestampWeekAgo,
            ),
            onNotificationClick = {},
            onUserClick = {},
            onMediaClick = {},
        )
    }
}

@OnTrackPreview
@Composable
fun RecommendNotificationCardPreview() {
    OnTrackTheme {
        NotificationCard(
            notification = NotificationData(
                type = NotificationType.RECOMMENDATION,
                senderUserId = previewUser.id,
                senderUserName = previewUser.name,
                senderUserImageUrl = previewUser.imageUrl,
                repliedToUserName = null,
                trackingId = "1",
                mediaId = previewMedia.id,
                mediaType = previewMedia.mediaType,
                mediaTitle = previewMedia.title,
                mediaImageUrl = previewMedia.coverUrl,
                timestamp = previewTimestampWeekAgo,
            ),
            onNotificationClick = {},
            onUserClick = {},
            onMediaClick = {},
        )
    }
}

@OnTrackPreview
@Composable
fun NotificationsScreenPreview() {
    OnTrackTheme {
        NotificationsScreen(
            viewModel = NotificationsViewModel(),
            onBack = {},
            onNotificationClick = {},
            onUserClick = {},
            onMediaClick = {},
        )
    }
}