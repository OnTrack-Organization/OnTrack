package de.ashman.ontrack.tracking.repository

import de.ashman.ontrack.tracking.domain.MediaType
import de.ashman.ontrack.tracking.domain.Tracking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TrackingRepository : JpaRepository<Tracking, UUID> {
    fun getTrackingsByUserId(userId: String): List<Tracking>

    fun findByUserIdInAndMediaIdAndMediaType(
        userIds: List<String>,
        mediaId: String,
        mediaType: MediaType
    ): List<Tracking>
}
