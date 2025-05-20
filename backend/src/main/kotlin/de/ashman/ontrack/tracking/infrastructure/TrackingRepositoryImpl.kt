package de.ashman.ontrack.tracking.infrastructure

import de.ashman.ontrack.tracking.domain.model.MediaType
import de.ashman.ontrack.tracking.domain.model.Tracking
import de.ashman.ontrack.tracking.domain.repository.TrackingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TrackingRepositoryImpl(
    private val trackingJpaRepository: TrackingJpaRepository,
) : TrackingRepository {
    override fun getTrackingsByUserId(userId: String): List<Tracking> {
        return trackingJpaRepository.getTrackingsByUserId(userId)
    }

    override fun getTrackingsByUserIdAndMedia(userIds: List<String>, mediaId: String, mediaType: MediaType): List<Tracking> {
        return trackingJpaRepository.findByUserIdInAndMediaIdAndMediaType(
            userIds = userIds,
            mediaId = mediaId,
            mediaType = mediaType,
        )
    }

    override fun save(tracking: Tracking) {
        trackingJpaRepository.save(tracking)
    }

    override fun delete(tracking: Tracking) {
        trackingJpaRepository.delete(tracking)
    }

    override fun getById(id: UUID): Tracking {
        return trackingJpaRepository.getReferenceById(id)
    }
}
