package de.ashman.ontrack.entity.tracking

import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.feed.Comment
import de.ashman.ontrack.domain.tracking.Entry
import de.ashman.ontrack.domain.feed.Like
import de.ashman.ontrack.entity.feed.CommentEntity
import de.ashman.ontrack.entity.feed.LikeEntity

fun Tracking.toEntity() = TrackingEntity(
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
    likes = likes.map { it.toEntity() },
    comments = comments.map { it.toEntity() },
    history = history.map { it.toEntity() },
    timestamp = timestamp,
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

fun Comment.toEntity() = CommentEntity(
    id = id,
    userId = userId,
    userImageUrl = userImageUrl,
    username = username,
    comment = comment,
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

fun Entry.toEntity() = EntryEntity(
    status = status,
    timestamp = timestamp,
)

fun EntryEntity.toDomain() = Entry(
    status = status,
    timestamp = timestamp,
)

fun Like.toEntity() = LikeEntity(
    userId = userId,
    username = username,
    userImageUrl = userImageUrl,
)

fun LikeEntity.toDomain() = Like(
    userId = userId,
    username = username,
    userImageUrl = userImageUrl,
)