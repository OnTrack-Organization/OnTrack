package de.ashman.ontrack.feature.tracking.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.review.repository.ReviewService
import de.ashman.ontrack.feature.tracking.controller.dto.CreateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.UpdateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.tracking.domain.toEntity
import de.ashman.ontrack.feature.tracking.repository.TrackingService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class TrackingController(
    val trackingService: TrackingService,
    val reviewService: ReviewService,
) {
    @GetMapping("trackings")
    fun getTrackingsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<TrackingDto>> {
        val trackings = trackingService.getTrackingsByUserId(identity.id)
        val trackingDtos = trackings.map { it.toDto() }

        return ResponseEntity.ok(trackingDtos)
    }

    @PostMapping("/tracking")
    @Transactional
    fun create(
        @RequestBody @Valid dto: CreateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<TrackingDto> {
        val newTracking = Tracking(
            userId = identity.id,
            status = dto.status,
            media = dto.media.toEntity(),
        )

        trackingService.save(newTracking)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newTracking.toDto())
    }

    @Transactional
    @PutMapping("tracking")
    fun update(
        @RequestBody @Valid dto: UpdateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<TrackingDto> {
        val tracking = trackingService.getById(dto.id)

        if (tracking.userId != identity.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        tracking.changeStatus(dto.status)

        return ResponseEntity.ok(tracking.toDto())
    }

    @DeleteMapping("tracking/{id}")
    @Transactional
    fun delete(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        val tracking = trackingService.getById(id)
        val review = reviewService.getByTrackingId(id)

        if (tracking.userId != identity.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        trackingService.delete(tracking)

        review?.let {
            reviewService.delete(it)
        }

        return ResponseEntity.ok().build()
    }
}

