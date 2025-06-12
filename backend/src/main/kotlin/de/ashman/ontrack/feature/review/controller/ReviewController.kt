package de.ashman.ontrack.feature.review.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.review.controller.dto.CreateReviewDto
import de.ashman.ontrack.feature.review.controller.dto.ReviewDto
import de.ashman.ontrack.feature.review.controller.dto.ReviewStatsDto
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.review.service.ReviewService
import de.ashman.ontrack.feature.tracking.domain.MediaType
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/review")
class ReviewController(
    private val reviewService: ReviewService,
) {
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getReviewsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): List<ReviewDto> {
        return reviewService.getReviewsByUserId(identity.id).map { it.toDto() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReview(
        @RequestBody dto: CreateReviewDto,
        @AuthenticationPrincipal identity: Identity
    ): ReviewDto {
        return reviewService.createReview(identity.id, dto).toDto()
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateReview(
        @RequestBody dto: CreateReviewDto,
        @AuthenticationPrincipal identity: Identity
    ): ReviewDto {
        return reviewService.updateReview(identity.id, dto).toDto()
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    fun getReviewStats(
        @RequestParam mediaType: MediaType,
        @RequestParam mediaId: String
    ): ReviewStatsDto {
        return reviewService.getReviewStats(mediaType, mediaId)
    }
}
