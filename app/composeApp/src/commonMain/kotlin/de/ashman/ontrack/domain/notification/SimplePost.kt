package de.ashman.ontrack.domain.notification

import de.ashman.ontrack.domain.tracking.Tracking

data class SimplePost(
    val id: String,
    val tracking: Tracking,
)
