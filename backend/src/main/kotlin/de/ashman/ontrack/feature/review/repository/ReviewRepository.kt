package de.ashman.ontrack.feature.review.repository

import de.ashman.ontrack.feature.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReviewRepository : JpaRepository<Review, UUID> {
    fun getReviewsByUserId(userId: String): List<Review>
    fun getByTrackingId(trackingId: UUID): Review?
    fun deleteAllByUserId(userId: String)
}