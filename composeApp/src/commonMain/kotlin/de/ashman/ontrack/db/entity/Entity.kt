package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.TrackStatus
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val displayName: String?,
    val username: String,
    val email: String?,
    val imageUrl: String? = null,
    val friends: List<String> = emptyList(),
    val createdAt: Long = System.now().toEpochMilliseconds(),
)

@Serializable
data class TrackingEntity(
    val id: String,

    val mediaType: MediaType,
    val mediaImageUrl: String,
    val mediaTitle: String,
    val mediaId: String,

    val status: TrackStatus? = null,
    // TODO change to double later
    val rating: Int? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val updatedAt: Long,

    val userId: String,
    val username: String,
    val userImageUrl: String,

    val likedBy: List<String> = listOf(),
    val comments: List<TrackingCommentEntity> = listOf(),
)

@Serializable
data class MediaEntity(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String? = null,
    val trackedCount: Int = 0,
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0,
    val updatedAt: Long = System.now().toEpochMilliseconds(),
)

@Serializable
data class TrackingCommentEntity(
    val id: String,
    val userId: String,
    val userImageUrl: String,
    val username: String,

    val comment: String,
    val timestamp: Long,
)