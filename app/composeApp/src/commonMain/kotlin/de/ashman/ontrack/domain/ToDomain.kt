package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.globalrating.RatingStats
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.tracking.Entry
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.globalrating.RatingStatsEntity
import de.ashman.ontrack.entity.share.CommentEntity
import de.ashman.ontrack.entity.share.LikeEntity
import de.ashman.ontrack.entity.tracking.EntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity

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
