package de.ashman.ontrack.feature.tracking.repository

import de.ashman.ontrack.feature.tracking.domain.MediaType
import de.ashman.ontrack.feature.tracking.domain.Tracking
import org.springframework.stereotype.Service
import java.util.*

@Service
class TrackingService(
    private val trackingRepository: TrackingRepository,
) {
    fun getTrackingsByUserId(userId: String): List<Tracking> = trackingRepository.getTrackingsByUserId(userId)

    fun getTrackingsByUserIdAndMedia(userIds: List<String>, mediaId: String, mediaType: MediaType): List<Tracking> =
        trackingRepository.findByUserIdInAndMediaIdAndMediaType(
            userIds = userIds,
            mediaId = mediaId,
            mediaType = mediaType,
        )

    fun save(tracking: Tracking) = trackingRepository.save(tracking)

    fun delete(tracking: Tracking) = trackingRepository.delete(tracking)

    fun getById(id: UUID): Tracking = trackingRepository.getReferenceById(id)
}
