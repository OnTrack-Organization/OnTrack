package de.ashman.ontrack.feature.recommendation.repository

import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.tracking.domain.MediaType
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val recommendationRepository: RecommendationRepository,
) {
    fun save(recommendation: Recommendation) = recommendationRepository.save(recommendation)

    fun findByReceiverAndMedia(receiverId: String, mediaId: String, mediaType: MediaType): List<Recommendation> {
        return recommendationRepository.findByReceiverIdAndMediaIdAndMediaType(receiverId, mediaId, mediaType)
    }

    fun findBySenderAndReceiverAndMedia(senderId: String, receiverId: String, mediaId: String, mediaType: MediaType): List<Recommendation> {
        return recommendationRepository.findBySenderIdAndReceiverIdAndMediaIdAndMediaType(senderId, receiverId, mediaId, mediaType)
    }
}
