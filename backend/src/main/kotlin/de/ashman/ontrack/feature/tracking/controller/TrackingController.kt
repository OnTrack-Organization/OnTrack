package de.ashman.ontrack.feature.tracking.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.review.repository.ReviewService
import de.ashman.ontrack.feature.share.service.PostService
import de.ashman.ontrack.feature.tracking.controller.dto.CreateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.UpdateTrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.tracking.domain.toEntity
import de.ashman.ontrack.feature.tracking.repository.TrackingService
import de.ashman.ontrack.feature.user.repository.UserService
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
    val userService: UserService,
    val postService: PostService,
) {
    @GetMapping("trackings")
    fun getTrackingsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<TrackingDto>> {
        val user = userService.getById(identity.id)
        val trackings = trackingService.getTrackingsByUserId(user.id)
        val trackingDtos = trackings.map { it.toDto() }

        return ResponseEntity.ok(trackingDtos)
    }

    @PostMapping("/tracking")
    @Transactional
    fun create(
        @RequestBody @Valid dto: CreateTrackingDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<TrackingDto> {
        val user = userService.getById(identity.id)

        val newTracking = Tracking(
            user = user,
            status = dto.status,
            media = dto.media.toEntity(),
        )

        trackingService.save(newTracking)

        postService.createPostForTracking(newTracking)

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
        val user = userService.getById(identity.id)
        val tracking = trackingService.getById(dto.id)

        if (tracking.user.id != user.id) {
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
        val user = userService.getById(identity.id)
        val tracking = trackingService.getById(id)
        val review = reviewService.getByTrackingId(id)

        val postId = postService.getPostIdByTrackingId(tracking.id)

        if (tracking.user.id != user.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        postId?.let { postService.deletePost(it) }
        trackingService.delete(tracking)
        review?.let { reviewService.delete(it) }

        return ResponseEntity.ok().build()
    }
}

