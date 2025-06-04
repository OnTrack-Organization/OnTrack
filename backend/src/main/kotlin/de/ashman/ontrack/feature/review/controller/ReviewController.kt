package de.ashman.ontrack.feature.review.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.review.controller.dto.CreateReviewDto
import de.ashman.ontrack.feature.review.controller.dto.ReviewDto
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.review.service.ReviewService
import jakarta.transaction.Transactional
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/review")
class ReviewController(
    private val reviewService: ReviewService,
) {
    @GetMapping("/all")
    fun getReviewsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): List<ReviewDto> {
        val reviewDtos = reviewService.getReviewsByUserId(identity.id).map { it.toDto() }
        return reviewDtos
    }

    @Transactional
    @PostMapping
    fun createReview(
        @RequestBody dto: CreateReviewDto,
        @AuthenticationPrincipal identity: Identity
    ): ReviewDto {
        val reviewDto = reviewService.createReview(identity.id, dto).toDto()
        return reviewDto
    }

    @Transactional
    @PutMapping
    fun updateReview(
        @RequestBody dto: CreateReviewDto,
        @AuthenticationPrincipal identity: Identity
    ): ReviewDto {
        val reviewDto = reviewService.updateReview(identity.id, dto).toDto()
        return reviewDto
    }
}
