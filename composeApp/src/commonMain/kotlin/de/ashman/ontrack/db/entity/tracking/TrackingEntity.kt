package de.ashman.ontrack.db.entity.tracking

import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEntity(
    val id: String,
    val userData: UserData,

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaCoverUrl: String,

    val status: TrackStatus,
    val rating: Double?,
    val reviewTitle: String?,
    val reviewDescription: String?,

    val updatedAt: Long,
) {
    fun toDomain(): Tracking =
        Tracking(
            id = id,
            userData = userData,
            mediaId = mediaId,
            mediaType = mediaType,
            mediaTitle = mediaTitle,
            mediaCoverUrl = mediaCoverUrl,
            status = status,
            rating = rating,
            reviewTitle = reviewTitle,
            reviewDescription = reviewDescription,
            updatedAt = updatedAt,
        )
}
