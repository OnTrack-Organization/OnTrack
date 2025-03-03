package de.ashman.ontrack.domain.recommendation

import de.ashman.ontrack.domain.media.MediaType

data class Recommendation(
    val id: String,
    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaCoverUrl: String?,
    val userId: String,
    val username: String,
    val userImageUrl: String,
    val message: String?,
    val status: RecommendationStatus,
    val timestamp: Long,
)

enum class RecommendationStatus {
    PENDING,
    CATALOG,
    PASS,
}