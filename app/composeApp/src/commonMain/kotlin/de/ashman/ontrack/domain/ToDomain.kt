package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.tracking.Entry
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.entity.globalrating.RatingStatsEntity
import de.ashman.ontrack.entity.share.CommentEntity
import de.ashman.ontrack.entity.share.LikeEntity
import de.ashman.ontrack.entity.tracking.EntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import de.ashman.ontrack.entity.user.FriendEntity
import de.ashman.ontrack.entity.user.FriendRequestEntity
import de.ashman.ontrack.entity.user.UserEntity
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain() =
    User(
        id = uid,
        fcmToken = "",
        email = email.orEmpty(),
        name = displayName.orEmpty(),
        username = displayName?.lowercase().orEmpty(),
        imageUrl = photoURL.orEmpty(),
    )

fun UserEntity.toDomain() =
    User(
        id = id,
        fcmToken = fcmToken,
        email = email,
        name = name,
        username = username,
        imageUrl = imageUrl,
    )

fun TrackingEntity.toDomain() = Tracking(
    id = id,
    mediaId = mediaId,
    mediaType = mediaType,
    mediaTitle = mediaTitle,
    mediaCoverUrl = mediaCoverUrl,
    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    userId = userId,
    username = username,
    userImageUrl = userImageUrl,
    likes = likes.map { it.toDomain() },
    comments = comments.map { it.toDomain() },
    history = history.map { it.toDomain() },
    timestamp = timestamp,
)

fun RatingStatsEntity.toDomain() = RatingStats(
    averageRating = averageRating,
    ratingCount = ratingCount
)

fun CommentEntity.toDomain() = Comment(
    id = id,
    userId = userId,
    userImageUrl = userImageUrl,
    username = username,
    name = name,
    comment = comment,
    timestamp = timestamp,
)

fun EntryEntity.toDomain() = Entry(
    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    timestamp = timestamp,
)

fun LikeEntity.toDomain() = Like(
    userId = userId,
    username = username,
    name = name,
    userImageUrl = userImageUrl,
)

fun FriendEntity.toDomain() =
    Friend(
        id = id,
        username = username,
        name = name,
        imageUrl = imageUrl,
    )

fun FriendRequestEntity.toDomain() =
    FriendRequest(
        userId = userId,
        username = username,
        name = name,
        imageUrl = imageUrl,
    )

fun Friend.toRequest() = FriendRequest(
    userId = id,
    name = name,
    username = username,
    imageUrl = imageUrl,
)