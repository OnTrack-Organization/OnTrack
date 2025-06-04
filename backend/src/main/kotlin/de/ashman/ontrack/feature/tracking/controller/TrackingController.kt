package de.ashman.ontrack.feature.tracking.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.tracking.controller.dto.CreateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.UpdateTrackingDto
import de.ashman.ontrack.feature.tracking.service.TrackingService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tracking")
class TrackingController(
    private val trackingService: TrackingService,
) {
    @GetMapping("/all")
    fun getTrackingsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<TrackingDto>> {
        val trackingDtos = trackingService.getTrackingsByUserId(identity.id)
        return ResponseEntity.ok(trackingDtos)
    }

    @PostMapping
    @Transactional
    fun create(
        @RequestBody @Valid dto: CreateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<TrackingDto> {
        val created = trackingService.createTracking(identity.id, dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping
    @Transactional
    fun update(
        @RequestBody @Valid dto: UpdateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<TrackingDto> {
        val updated = trackingService.updateTracking(identity.id, dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    @Transactional
    fun delete(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        trackingService.deleteTracking(identity.id, id)
        return ResponseEntity.ok().build()
    }
}
