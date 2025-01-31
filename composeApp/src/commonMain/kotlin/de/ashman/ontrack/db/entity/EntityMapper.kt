package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.Videogame
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Media.toEntity(): MediaEntity {
    return MediaEntity(
        id = id,
        name = title,
        coverUrl = coverUrl,
        type = mediaType,
        trackStatus = trackStatus?.toEntity(),
    )
}

fun TrackStatus.toEntity() = TrackStatusEntity(
    statusType = statusType,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
)

fun TrackStatusEntity.toDomain() = TrackStatus(
    statusType = statusType,
    rating = rating,
    reviewTitle = reviewTitle,
    reviewDescription = reviewDescription,
    timestamp = timestamp.formatDate(),
)

fun MediaEntity.toDomain(): Media {
    return when (this.type) {
        MediaType.MOVIE -> Movie(
            id = id,
            title = name,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.SHOW -> Show(
            id = id,
            title = name,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.BOOK -> Book(
            id = id,
            title = name,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.VIDEOGAME -> Videogame(
            id = id,
            title = name,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.BOARDGAME -> Boardgame(
            id = id,
            title = name,
            coverUrl = coverUrl,
            mediaType = type,
        )

        MediaType.ALBUM -> Album(
            id = id,
            title = name,
            coverUrl = coverUrl,
            mediaType = type,
        )
    }
}


fun LocalDateTime.formatDate(): String {
    return "${this.dayOfMonth.toString().padStart(2, '0')}.${this.monthNumber.toString().padStart(2, '0')}.${this.year}"
}