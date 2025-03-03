package de.ashman.ontrack.entity.tracking

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.entity.feed.CommentEntity
import de.ashman.ontrack.entity.feed.LikeEntity
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TrackingEntity(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),

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

    val likes: List<LikeEntity> = emptyList(),
    val comments: List<CommentEntity> = emptyList(),
    val history: List<EntryEntity> = emptyList(),

    val timestamp: Long = System.now().toEpochMilliseconds(),
)
