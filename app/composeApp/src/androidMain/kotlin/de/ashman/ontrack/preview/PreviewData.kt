package de.ashman.ontrack.preview

import androidx.compose.ui.tooling.preview.Preview
import de.ashman.ontrack.domain.media.Show
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
annotation class OnTrackPreview

val previewStatus = TrackStatus.CONSUMED

val previewUser = User(
    id = "1",
    username = "za_warudo_brando",
    fcmToken = "test_token",
    name = "Dio Brando",
    imageUrl = "",
    email = "dio.brando@theworld.com"
)

val previewMedia = Show(
    id = "1",
    title = "Jojo's Bizarre Adventure",
    coverUrl = null,
    detailUrl = "",
)

val previewTimestampNow = System.currentTimeMillis()
val previewTimestampHourAgo = System.currentTimeMillis() - 60 * 60 * 1000
val previewTimestampYesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000
val previewTimestampWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000

val previewLike = Like(
    userId = previewUser.id,
    username = previewUser.username,
    userImageUrl = previewUser.imageUrl,
    name = previewUser.name
)

val previewComment = Comment(
    userId = previewUser.id,
    userImageUrl = previewUser.imageUrl,
    username = previewUser.username,
    name = previewUser.name,
    comment = "This is a comment",
    timestamp = previewTimestampNow,
)

val previewTracking = Tracking(
    mediaId = previewMedia.id,
    mediaType = previewMedia.mediaType,
    mediaTitle = previewMedia.title,
    mediaCoverUrl = previewMedia.coverUrl,
    status = previewStatus,
    username = previewUser.name,
    userId = previewUser.id,
    userImageUrl = previewUser.imageUrl,
    rating = 5.0,
    reviewTitle = "Review title",
    reviewDescription = "A short review description",
    timestamp = previewTimestampWeekAgo,
    likes = listOf(previewLike),
    comments = listOf(previewComment),
)

