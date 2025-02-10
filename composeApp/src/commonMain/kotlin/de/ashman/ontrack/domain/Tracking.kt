package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@CommonParcelize
@Serializable
data class Tracking @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),

    val mediaType: MediaType,
    val mediaImageUrl: String,
    val mediaTitle: String,
    val mediaId: String,

    val status: TrackStatus? = null,
    // TODO change to double later
    val rating: Int? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val timestamp: Long = System.now().toEpochMilliseconds(),

    val userId: String,
    val username: String,
    val userImageUrl: String,

    val likedBy: List<String> = listOf(),
    val comments: List<TrackingComment> = listOf(),
) : CommonParcelable

enum class TrackStatus {
    CATALOG, CONSUMING, CONSUMED, DROPPED
}

const val MAX_RATING = 5

@CommonParcelize
@Serializable
data class TrackingComment(
    val id: String,
    val userId: String,
    val userImageUrl: String,
    val username: String,

    val comment: String,
    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable