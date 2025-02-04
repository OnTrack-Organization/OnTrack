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
    val mediaId: String,
    val status: TrackStatus? = null,
    // TODO change to double later
    val rating: Int? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,
    val updatedAt: Long,
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
