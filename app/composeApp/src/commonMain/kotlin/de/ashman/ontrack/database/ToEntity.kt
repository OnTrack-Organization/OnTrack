package de.ashman.ontrack.database

import de.ashman.ontrack.domain.newdomains.NewTracking

fun NewTracking.toEntity() = NewTrackingEntity(
    id = id,
    userId = userId,
    media = media,
    status = status,
    timestamp = timestamp,
)