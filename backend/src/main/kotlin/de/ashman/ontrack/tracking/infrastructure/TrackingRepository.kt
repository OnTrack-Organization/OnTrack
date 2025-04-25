package de.ashman.ontrack.tracking.infrastructure

import de.ashman.ontrack.tracking.domain.model.Tracking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TrackingRepository : JpaRepository<Tracking, UUID> {
    fun getTrackingsByUserId(userId: String): List<Tracking>
}
