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
    statusType = statusType,
    timestamp = Clock.System.now().toEpochMilliseconds(),
    rating = rating,
    review = review,
)

fun TrackStatusEntity.toDomain() = TrackStatus(
    id = id,
    statusType = statusType,
    rating = rating,
    review = review,
)