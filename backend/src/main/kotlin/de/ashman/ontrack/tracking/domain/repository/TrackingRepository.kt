package de.ashman.ontrack.tracking.domain.repository

import de.ashman.ontrack.tracking.domain.model.MediaType
import de.ashman.ontrack.tracking.domain.model.Tracking
import java.util.UUID

interface TrackingRepository {
    fun getTrackingsByUserId(userId: String): List<Tracking>
    fun getTrackingsByUserIdAndMedia(
        userIds: List<String>,
        mediaId: String,
        mediaType: MediaType
    ): List<Tracking>
    fun save(tracking: Tracking)
    fun delete(tracking: Tracking)
    fun getById(id: UUID): Tracking
}
