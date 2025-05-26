package de.ashman.ontrack.feature.review.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.review.controller.dto.CreateReviewDto
import de.ashman.ontrack.feature.review.controller.dto.ReviewDto
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.review.repository.ReviewService
import de.ashman.ontrack.feature.share.service.PostService
import de.ashman.ontrack.feature.tracking.repository.TrackingService
import de.ashman.ontrack.feature.user.repository.UserService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ReviewController(
    val reviewService: ReviewService,
    val userService: UserService,
    val trackingService: TrackingService,
    val postService: PostService,
) {
    @GetMapping("reviews")
    fun getReviewsOfCurrentUser(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<ReviewDto>> {
        val reviews = reviewService.getReviewsByUserId(identity.id)
        val reviewDtos = reviews.map { it.toDto() }

        return ResponseEntity.ok(reviewDtos)
    }

    @Transactional
    @PostMapping("review")
    fun createReview(
        @RequestBody dto: CreateReviewDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<ReviewDto> {
        val user = userService.getById(identity.id)
        val tracking = trackingService.getById(dto.trackingId)

        if (tracking.user.id != user.id) {
            return ResponseEntity.status(403).build()
        }

        val newReview = Review(
            user = user,
            tracking = tracking,
            rating = dto.rating,
            title = dto.title,
            description = dto.description,
        )

        reviewService.save(newReview)

        postService.updatePostWithReview(newReview)

        return ResponseEntity.ok(newReview.toDto())
    }

    @Transactional
    @PutMapping("review")
    fun updateReview(
        @RequestBody dto: CreateReviewDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<ReviewDto> {
        val user = userService.getById(identity.id)
        val review = reviewService.getByTrackingId(dto.trackingId)

        if (review?.user?.id != user.id) {
            return ResponseEntity.status(403).build()
        }

        review.update(dto.rating, dto.title, dto.description)

        return ResponseEntity.ok(review.toDto())
    }
}