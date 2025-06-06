package de.ashman.ontrack.feature.tracking.service

import de.ashman.ontrack.feature.share.service.PostService
import de.ashman.ontrack.feature.tracking.controller.dto.CreateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.UpdateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.tracking.domain.toEntity
import de.ashman.ontrack.feature.tracking.repository.TrackingRepository
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*

@Service
class TrackingService(
    private val trackingRepository: TrackingRepository,
    private val userRepository: UserRepository,
    private val postService: PostService,
) {
    fun getTrackingsByUserId(userId: String): List<TrackingDto> {
        val user = userRepository.getReferenceById(userId)

        return trackingRepository.getTrackingsByUserId(user.id).map { it.toDto() }
    }

    fun createTracking(userId: String, dto: CreateTrackingDto): TrackingDto {
        val user = userRepository.getReferenceById(userId)

        val newTracking = Tracking(
            user = user,
            status = dto.status,
            media = dto.media.toEntity(),
        )

        trackingRepository.save(newTracking)
        postService.createPostForTracking(newTracking)

        return newTracking.toDto()
    }

    fun updateTracking(userId: String, dto: UpdateTrackingDto): TrackingDto {
        val user = userRepository.getReferenceById(userId)
        val tracking = trackingRepository.getReferenceById(dto.id)

        if (tracking.user.id != user.id) {
            throw AccessDeniedException("You are not allowed to update this tracking.")
        }

        tracking.status = dto.status

        return tracking.toDto()
    }

    fun deleteTracking(trackingId: UUID) {
        val tracking = trackingRepository.getReferenceById(trackingId)

        trackingRepository.delete(tracking)
    }
}
