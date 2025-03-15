package de.ashman.ontrack.domain.recommendation

import de.ashman.ontrack.domain.media.MediaType
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Recommendation(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),
    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaCoverUrl: String?,
    val userId: String,
    val username: String,
    val userImageUrl: String,
    val message: String?,
    val status: RecommendationStatus,
    val timestamp: Long = System.now().toEpochMilliseconds(),
)

enum class RecommendationStatus {
    PENDING,
    CATALOG,
    PASS,
}