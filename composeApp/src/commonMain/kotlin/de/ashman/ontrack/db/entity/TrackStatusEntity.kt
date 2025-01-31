package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.TrackStatusType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TrackStatusEntity(
    val statusType: TrackStatusType?,
    val rating: Int?,
    val reviewTitle: String?,
    val reviewDescription: String?,
    val timestamp: LocalDateTime,
)

/*

@Serializable
data class TrackStatusEntity(
    val id: String,
    val userId: String,              // The ID of the user tracking this media
    val mediaId: String, // The ID of the media (movie, show, etc.)
    val rating: Int? = null,      // Optional rating given by the user (0.0 to 5.0)
    val status: TrackStatusType,          // The status (WATCHED, IN_PROGRESS, etc.)
    val reviewTitle: String? = null, // The title of the review (optional)
    val reviewDescription: String? = null, // The description of the review (optional)
    val mediaName: String? = null,   // The name of the media (e.g., "The Matrix")
    val mediaImageUrl: String? = null, // The image URL of the media (optional)
    val timestamp: Long = System.now().toEpochMilliseconds() // Timestamp when the status was added
)*/
