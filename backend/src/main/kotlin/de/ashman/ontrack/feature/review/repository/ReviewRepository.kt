package de.ashman.ontrack.feature.review.repository

import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.tracking.domain.MediaType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReviewRepository : JpaRepository<Review, UUID> {
    fun getReviewsByUserId(userId: String): List<Review>
    fun getByTrackingId(trackingId: UUID): Review?

    @Query(
        """
    SELECT r FROM Review r
    WHERE r.tracking.media.type = :mediaType
    AND r.tracking.media.id = :mediaId
        """
    )
    fun findByMedia(mediaType: MediaType, mediaId: String): List<Review>

}