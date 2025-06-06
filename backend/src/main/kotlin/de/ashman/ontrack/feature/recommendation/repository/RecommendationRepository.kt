package de.ashman.ontrack.feature.recommendation.repository

import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.tracking.domain.MediaType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RecommendationRepository : JpaRepository<Recommendation, UUID> {
    fun findByReceiverIdAndMediaIdAndMediaType(
        receiverId: String,
        mediaId: String,
        mediaType: MediaType
    ): List<Recommendation>

    fun findBySenderIdAndReceiverIdAndMediaIdAndMediaType(
        senderId: String,
        receiverId: String,
        mediaId: String,
        mediaType: MediaType
    ): List<Recommendation>
}