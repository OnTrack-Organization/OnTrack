package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEntity(
    val id: String,
    val userId: String,
    val timestamp: Long,

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String?,
    val mediaCoverUrl: String?,

    val status: TrackStatus?,
    val rating: Double?,
    val reviewTitle: String?,
    val reviewDescription: String?,

    val likes: List<LikeEntity>,
    val comments: List<CommentEntity>,
    val history: List<HistoryEntryEntity>,
)

@Serializable
data class LikeEntity(
    val id: String,
    val userId: String,
    val timestamp: Long,
) {
    // Needed for Ios...
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "timestamp" to timestamp,
        )
    }
}

@Serializable
data class CommentEntity(
    val id: String,
    val userId: String,
    val comment: String,
    val timestamp: Long,
) {
    // Needed for Ios...
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "comment" to comment,
            "timestamp" to timestamp,
        )
    }
}

@Serializable
data class HistoryEntryEntity(
    val status: TrackStatus,
    val timestamp: Long,
)