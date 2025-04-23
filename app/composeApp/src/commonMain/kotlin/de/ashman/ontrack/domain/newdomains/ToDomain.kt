package de.ashman.ontrack.domain.newdomains

import de.ashman.ontrack.database.NewTrackingEntity

fun NewTrackingEntity.toDomain() = NewTracking(
    id = id,
    userId = userId,
    media = media,
    status = status,
    timestamp = timestamp,
)