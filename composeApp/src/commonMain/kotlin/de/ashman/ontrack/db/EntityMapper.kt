package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.tracking.TrackingComment

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
    likedBy = likedBy,
    comments = comments.map { it.toEntity() },
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
    likedBy = likedBy,
    comments = comments.map { it.toDomain() },
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
