package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEntity(
    val id: String,

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String?,
    val mediaCoverUrl: String?,

    val status: TrackStatus?,
    val rating: Double?,
    val reviewTitle: String?,
    val reviewDescription: String?,

    val userId: String,
    val username: String,
    val userImageUrl: String,

    val likes: List<TrackingLikeEntity>,
    val comments: List<TrackingCommentEntity>,
    val history: List<TrackingHistoryEntryEntity>,

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

@Serializable
data class TrackingHistoryEntryEntity(
    val status: TrackStatus,
    val timestamp: Long,
)

@Serializable
data class TrackingLikeEntity(
    val userId: String,
    val username: String,
    val userImageUrl: String,
)