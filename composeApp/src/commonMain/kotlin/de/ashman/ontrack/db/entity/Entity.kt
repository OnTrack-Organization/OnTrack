package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
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
    val mediaType: MediaType,
    val mediaTitle: String?,
    val mediaCoverUrl: String?,

    val status: TrackStatus?,
    // TODO change to double later
    val rating: Int?,
    val reviewTitle: String?,
    val reviewDescription: String?,

    val userId: String?,
    val username: String?,
    val userImageUrl: String?,

    val likedBy: List<String>,
    val comments: List<TrackingCommentEntity>,

    val timestamp: Long,
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