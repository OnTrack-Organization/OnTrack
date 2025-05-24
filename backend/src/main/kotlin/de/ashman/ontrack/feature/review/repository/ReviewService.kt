package de.ashman.ontrack.feature.review.repository

import de.ashman.ontrack.feature.review.domain.Review
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
) {
    fun getReviewsByUserId(userId: String): List<Review> = reviewRepository.getReviewsByUserId(userId)
    fun getByTrackingId(trackingId: UUID): Review? = reviewRepository.getByTrackingId(trackingId)
    fun save(review: Review) = reviewRepository.save(review)
    fun delete(review: Review) = reviewRepository.delete(review)
}