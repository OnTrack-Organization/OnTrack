package de.ashman.ontrack.domain.sub

import de.ashman.ontrack.db.entity.TrackStatusEntity

fun TrackStatus.toEntity() = TrackStatusEntity(
    id = id,
    status = status,
    timestamp = timestamp,
    rating = rating,
    review = review,
)

fun TrackStatusEntity.toDomain() = TrackStatus(
    id = id,
    status = status,
    timestamp = timestamp,
    rating = rating,
    review = review,
)