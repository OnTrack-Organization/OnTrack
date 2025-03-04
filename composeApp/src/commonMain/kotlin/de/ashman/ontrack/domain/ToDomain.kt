package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.feed.Comment
import de.ashman.ontrack.domain.feed.Like
import de.ashman.ontrack.domain.tracking.Entry
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.entity.feed.CommentEntity
import de.ashman.ontrack.entity.feed.LikeEntity
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
        name = displayName,
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

fun CommentEntity.toDomain() = Comment(
    id = id,
    userId = userId,
    userImageUrl = userImageUrl,
    username = username,
    comment = comment,
    timestamp = timestamp,
)

fun EntryEntity.toDomain() = Entry(
    status = status,
    timestamp = timestamp,
)

fun LikeEntity.toDomain() = Like(
    userId = userId,
    username = username,
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
        status = status,
    )

fun Friend.toRequest() = FriendRequest(
    userId = id,
    name = name,
    username = username,
    imageUrl = imageUrl,
)