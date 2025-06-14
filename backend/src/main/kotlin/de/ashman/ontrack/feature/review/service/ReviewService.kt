package de.ashman.ontrack.feature.review.service

import de.ashman.ontrack.feature.review.controller.dto.CreateReviewDto
import de.ashman.ontrack.feature.review.controller.dto.ReviewStatsDto
import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.review.repository.ReviewRepository
import de.ashman.ontrack.feature.share.service.PostService
import de.ashman.ontrack.feature.tracking.domain.MediaType
import de.ashman.ontrack.feature.tracking.repository.TrackingRepository
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val trackingRepository: TrackingRepository,
    private val postService: PostService,
) {
    fun getReviewsByUserId(userId: String): List<Review> = reviewRepository.getReviewsByUserId(userId)

    @Transactional
    fun createReview(userId: String, dto: CreateReviewDto): Review {
        val user = userRepository.getReferenceById(userId)
        val tracking = trackingRepository.getReferenceById(dto.trackingId)

        val newReview = Review(
            user = user,
            tracking = tracking,
            rating = dto.rating,
            title = dto.title,
            description = dto.description,
        )

        reviewRepository.save(newReview)
        postService.updatePostWithReview(newReview)

        return newReview
    }

    @Transactional
    fun updateReview(userId: String, dto: CreateReviewDto): Review {
        val review = reviewRepository.getByTrackingId(dto.trackingId) ?: throw IllegalArgumentException("Review not found for tracking")

        review.update(dto.rating, dto.title, dto.description)

        return review
    }

    fun getReviewStats(mediaType: MediaType, mediaId: String): ReviewStatsDto {
        val reviews = reviewRepository.findByMedia(mediaType, mediaId)
        val ratings = reviews.map { it.rating }

        val average = if (ratings.isEmpty()) {
            0.0
        } else {
            ratings.sum() / ratings.size
        }

        return ReviewStatsDto(
            count = ratings.size,
            average = average
        )
    }
}
