package de.ashman.ontrack.entity.tracking

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.entity.feed.CommentEntity
import de.ashman.ontrack.entity.feed.LikeEntity
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEntity(
    val id: String,

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaCoverUrl: String?,

    val status: TrackStatus?,
    val rating: Double?,
    val reviewTitle: String?,
    val reviewDescription: String?,

    val userId: String,
    val username: String,
    val userImageUrl: String,

    val likes: List<LikeEntity>,
    val comments: List<CommentEntity>,
    val history: List<EntryEntity>,

    val timestamp: Long,
)
