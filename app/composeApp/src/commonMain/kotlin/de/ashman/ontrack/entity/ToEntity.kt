package de.ashman.ontrack.entity

import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.tracking.Entry
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.share.CommentEntity
import de.ashman.ontrack.entity.share.LikeEntity
import de.ashman.ontrack.entity.tracking.EntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity

fun Tracking.toEntity() = TrackingEntity(
    id = id,
    mediaId = mediaId,
    mediaType = mediaType,
    mediaTitle = mediaTitle,
    mediaCoverUrl = mediaCoverUrl,
    status = status!!,
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

fun Tracking.toEntryEntity() = EntryEntity(
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    status = status!!,
    timestamp = timestamp,
)

fun Comment.toEntity() = CommentEntity(
    id = id,
    userId = userId,
    userImageUrl = userImageUrl,
    username = username,
    name = name,
    comment = comment,
    timestamp = timestamp,
)

fun Entry.toEntity() = EntryEntity(
    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    timestamp = timestamp,
)

fun Like.toEntity() = LikeEntity(
    userId = userId,
    username = username,
    name = name,
    userImageUrl = userImageUrl,
)
