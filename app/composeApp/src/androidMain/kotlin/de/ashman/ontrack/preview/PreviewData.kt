package de.ashman.ontrack.preview

import androidx.compose.ui.tooling.preview.Preview
import de.ashman.ontrack.domain.media.MediaData
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.User

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
annotation class OnTrackPreview

val previewStatus = TrackStatus.CONSUMED

val previewUser = User(
    id = "1",
    username = "za_warudo_brando",
    name = "Dio Brando",
    profilePictureUrl = "",
    email = "dio.brando@theworld.com"
)

val previewMedia = MediaData(
    id = "1",
    type = MediaType.SHOW,
    title = "Jojo's Bizarre Adventure",
    coverUrl = null,
)

val previewTimestampNow = System.currentTimeMillis()
val previewTimestampHourAgo = System.currentTimeMillis() - 60 * 60 * 1000
val previewTimestampYesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000
val previewTimestampWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000

val previewLike = Like(
    id = "1",
    postId = "1",
    user = previewUser,
    timestamp = previewTimestampNow,
)

val previewComment = Comment(
    id = "1",
    postId = "1",
    user = previewUser,
    message = "This is a comment",
    mentionedUsers = listOf(previewUser),
    likeCount = 0,
    postedByCurrentUser = false,
    deletable = false,
    timestamp = previewTimestampNow,
)

val previewTracking = Tracking(
    id = "1",
    userId = previewUser.id,
    media = previewMedia,
    status = previewStatus,
    timestamp = previewTimestampNow,
)

val previewPost = Post(
    id = "1",
    user = previewUser,
    tracking = previewTracking,
    review = null,
    likedByCurrentUser = false,
    likes = listOf(previewLike),
    comments = listOf(previewComment),
    likeCount = 1,
    commentCount = 1,
)