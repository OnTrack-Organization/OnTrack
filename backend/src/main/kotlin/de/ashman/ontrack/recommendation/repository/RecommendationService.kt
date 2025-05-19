package de.ashman.ontrack.recommendation.repository

import de.ashman.ontrack.recommendation.domain.Recommendation
import de.ashman.ontrack.tracking.domain.model.MediaType
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val recommendationRepository: RecommendationRepository,
) {
    fun save(recommendation: Recommendation) = recommendationRepository.save(recommendation)

    fun findByReceiverAndMedia(receiverId: String, mediaId: String, mediaType: MediaType): List<Recommendation> {
        return recommendationRepository.findByReceiverIdAndMediaIdAndMediaType(receiverId, mediaId, mediaType)
    }
}
