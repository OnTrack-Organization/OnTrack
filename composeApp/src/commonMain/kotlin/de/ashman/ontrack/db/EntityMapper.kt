package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.MediaEntity
import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.Tracking
import de.ashman.ontrack.domain.TrackingComment
import de.ashman.ontrack.domain.Videogame
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Tracking.toEntity() = TrackingEntity(
    id = id,
    mediaId = mediaId,
    mediaType = mediaType,
    mediaImageUrl = mediaImageUrl,
    mediaTitle = mediaTitle,

    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    updatedAt = timestamp,

    userId = userId,
    username = username,
    userImageUrl = userImageUrl,

    likedBy = likedBy,
    comments = comments.map { it.toEntity() },
)

fun TrackingEntity.toDomain() = Tracking(
    id = id,
    mediaId = mediaId,
    mediaType = mediaType,
    mediaImageUrl = mediaImageUrl,
    mediaTitle = mediaTitle,

    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,

    timestamp = updatedAt,

    userId = userId,
    username = username,
    userImageUrl = userImageUrl,

    likedBy = likedBy,
    comments = comments.map { it.toDomain() },
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
)

fun Media.toEntity() = MediaEntity(
    id = id,
    type = mediaType,
    title = title,
    coverUrl = coverUrl,
)

fun MediaEntity.toDomain(): Media =
    when (this.type) {
        MediaType.MOVIE -> Movie(
            id = id,
            title = title,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.SHOW -> Show(
            id = id,
            title = title,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.BOOK -> Book(
            id = id,
            title = title,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.VIDEOGAME -> Videogame(
            id = id,
            title = title,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.BOARDGAME -> Boardgame(
            id = id,
            title = title,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.ALBUM -> Album(
            id = id,
            title = title,
            coverUrl = coverUrl,
            mediaType = type,
        )
    }
