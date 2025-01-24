package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.TrackStatus
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

fun LocalDateTime.formatDate() : String {
    return "${this.dayOfMonth.toString().padStart(2, '0')}.${this.monthNumber.toString().padStart(2, '0')}.${this.year}"
}