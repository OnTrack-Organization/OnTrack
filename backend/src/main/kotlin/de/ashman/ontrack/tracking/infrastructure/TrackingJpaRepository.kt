package de.ashman.ontrack.tracking.infrastructure

import de.ashman.ontrack.tracking.domain.model.MediaType
import de.ashman.ontrack.tracking.domain.model.Tracking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

interface TrackingJpaRepository : JpaRepository<Tracking, UUID> {
    fun getTrackingsByUserId(userId: String): List<Tracking>

    fun findByUserIdInAndMediaIdAndMediaType(
        userIds: List<String>,
        mediaId: String,
        mediaType: MediaType
    ): List<Tracking>
}
