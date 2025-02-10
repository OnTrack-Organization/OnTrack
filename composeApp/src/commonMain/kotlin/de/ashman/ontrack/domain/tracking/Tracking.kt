package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@CommonParcelize
@Serializable
data class Tracking(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String? = null,
    val mediaCoverUrl: String? = null,

    val status: TrackStatus? = null,
    // TODO change to double later
    val rating: Int? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val userId: String? = null,
    val username: String? = null,
    val userImageUrl: String? = null,

    val likedBy: List<String> = listOf(),
    val comments: List<TrackingComment> = listOf(),

    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable

enum class TrackStatus {
    CATALOG, CONSUMING, CONSUMED, DROPPED
}

const val MAX_RATING = 5
