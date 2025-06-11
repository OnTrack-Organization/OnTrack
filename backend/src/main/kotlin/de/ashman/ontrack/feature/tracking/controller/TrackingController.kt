package de.ashman.ontrack.feature.tracking.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.tracking.controller.dto.CreateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.UpdateTrackingDto
import de.ashman.ontrack.feature.tracking.service.TrackingService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tracking")
class TrackingController(
    private val trackingService: TrackingService,
) {
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getTrackingsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): List<TrackingDto> {
        return trackingService.getTrackingsByUserId(identity.id)
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid dto: CreateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): TrackingDto {
        return trackingService.createTracking(identity.id, dto)
    }

    @PutMapping
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @RequestBody @Valid dto: UpdateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): TrackingDto {
        return trackingService.updateTracking(identity.id, dto)
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ) {
        trackingService.deleteTracking(id)
    }
}
