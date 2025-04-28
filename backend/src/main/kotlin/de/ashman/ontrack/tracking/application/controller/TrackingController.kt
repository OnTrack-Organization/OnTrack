package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.tracking.domain.model.Media
import de.ashman.ontrack.tracking.domain.model.Tracking
import de.ashman.ontrack.tracking.infrastructure.TrackingRepository
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class TrackingController(
    val trackingRepository: TrackingRepository
) {
    @PostMapping("/tracking")
    @Transactional
    fun create(
        @RequestBody @Valid dto: CreateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<TrackingDto> {
        val newTracking = Tracking(
            identity.id,
            dto.status,
            Media(
                dto.media.id,
                dto.media.type,
                dto.media.title,
                dto.media.coverUrl
            )
        )

        trackingRepository.save(newTracking)

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
        val tracking = trackingRepository.getReferenceById(dto.id)
        if (tracking.userId != identity.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        tracking.changeStatus(dto.status)

        return ResponseEntity.ok(tracking.toDto())
    }

    @GetMapping("trackings")
    fun getTrackingsOfLoggedInUser(@AuthenticationPrincipal identity: Identity): ResponseEntity<List<TrackingDto>> {
        val trackings = trackingRepository.getTrackingsByUserId(identity.id)
        val trackingDtos = trackings.stream().map { it.toDto() }.toList()

        return ResponseEntity.ok(trackingDtos)
    }

    @DeleteMapping("tracking/{id}")
    @Transactional
    fun delete(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        val tracking = trackingRepository.getReferenceById(id)
        if (tracking.userId != identity.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        trackingRepository.delete(tracking)

        return ResponseEntity.ok().build()
    }
}

