package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.tracking.Comment
import de.ashman.ontrack.domain.tracking.HistoryEntry
import de.ashman.ontrack.domain.tracking.Like
import de.ashman.ontrack.domain.tracking.Tracking

fun Tracking.toEntity() = TrackingEntity(
    id = id,
    userId = userId,
    timestamp = timestamp,
    mediaId = mediaId,
    mediaType = mediaType,
    mediaTitle = mediaTitle,
    mediaCoverUrl = mediaCoverUrl,
    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    likes = likes.map { it.toEntity() },
    comments = comments.map { it.toEntity() },
    history = history.map { it.toEntity() },
)

fun TrackingEntity.toDomain(userName: String, userImageUrl: String): Tracking {
    return Tracking(
        id = id,
        userId = userId,
        username = userName,
        userImageUrl = userImageUrl,
        timestamp = timestamp,
        mediaId = mediaId,
        mediaType = mediaType,
        mediaTitle = mediaTitle,
        mediaCoverUrl = mediaCoverUrl,
        status = status,
        rating = rating,
        reviewTitle = reviewTitle,
        reviewDescription = reviewDescription,
        likes = likes.map { it.toDomain() },
        comments = comments.map { it.toDomain() },
        history = history.map { it.toDomain() },
    )
}

fun Comment.toEntity() = CommentEntity(
    id = id,
    userId = userId,
    comment = comment,
    timestamp = timestamp,
)

fun CommentEntity.toDomain() = Comment(
    id = id,
    userId = userId,
    comment = comment,
    timestamp = timestamp,
)

fun HistoryEntry.toEntity() = HistoryEntryEntity(
    status = status,
    timestamp = timestamp,
)

fun HistoryEntryEntity.toDomain() = HistoryEntry(
    status = status,
    timestamp = timestamp,
)

fun Like.toEntity() = LikeEntity(
    id = id,
    userId = userId,
    timestamp = timestamp,
)

fun LikeEntity.toDomain() = Like(
    id = id,
    userId = userId,
    timestamp = timestamp,
)