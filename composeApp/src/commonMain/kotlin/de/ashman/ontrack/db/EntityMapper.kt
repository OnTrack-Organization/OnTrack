package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.MediaEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.Tracking
import de.ashman.ontrack.domain.Videogame
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Tracking.toEntity() = TrackingEntity(
    id = id,
    mediaId = mediaId,
    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    updatedAt = timestamp,
)

fun TrackingEntity.toDomain() = Tracking(
    id = id,
    mediaId = mediaId,
    status = status,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    timestamp = updatedAt,
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
