package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.TrackStatus
import kotlinx.datetime.Clock

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
    id = id,
    timestamp = Clock.System.now().toEpochMilliseconds(),
    statusType = statusType,
    rating = rating,
    review = review,
)

fun TrackStatusEntity.toDomain() = TrackStatus(
    id = id,
    timestamp = timestamp,
    statusType = statusType,
    rating = rating,
    review = review,
)