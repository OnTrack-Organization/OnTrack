package de.ashman.ontrack.domain.recommendation

import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.user.User

data class SimpleTracking(
    val user: User,
    val status: TrackStatus,
    val review: Review?,
    val timestamp: Long,
)