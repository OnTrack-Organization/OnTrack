package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.tracking.TrackingComment
import de.ashman.ontrack.domain.tracking.TrackingHistoryEntry
import de.ashman.ontrack.domain.tracking.TrackingLike

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

fun TrackingComment.toEntity() = TrackingCommentEntity(
    id = id,
    userId = userId,
    userImageUrl = userImageUrl,
    username = username,
    comment = comment,
    timestamp = timestamp,
)

fun TrackingCommentEntity.toDomain() = TrackingComment(
    id = id,
    userId = userId,
    userImageUrl = userImageUrl,
    username = username,
    comment = comment,
    timestamp = timestamp,
)

fun TrackingHistoryEntry.toEntity() = TrackingHistoryEntryEntity(
    status = status,
    timestamp = timestamp,
)

fun TrackingHistoryEntryEntity.toDomain() = TrackingHistoryEntry(
    status = status,
    timestamp = timestamp,
)

fun TrackingLike.toEntity() = TrackingLikeEntity(
    userId = userId,
    username = username,
    userImageUrl = userImageUrl,
)

fun TrackingLikeEntity.toDomain() = TrackingLike(
    userId = userId,
    username = username,
    userImageUrl = userImageUrl,
)