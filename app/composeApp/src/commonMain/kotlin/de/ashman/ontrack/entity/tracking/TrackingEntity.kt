package de.ashman.ontrack.entity.tracking

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.entity.share.CommentEntity
import de.ashman.ontrack.entity.share.LikeEntity
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEntity(
    val id: String,

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaCoverUrl: String?,

    val status: TrackStatus,
    val rating: Double? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val userId: String,
    val username: String,
    val userImageUrl: String,

    // TODO move into sub collection
    val likes: List<LikeEntity> = emptyList(),
    val comments: List<CommentEntity> = emptyList(),
    val history: List<EntryEntity> = emptyList(),

    val timestamp: Long = System.now().toEpochMilliseconds(),
)
