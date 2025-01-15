package de.ashman.ontrack.util

import de.ashman.ontrack.domain.sub.MediaType
import kotlinx.serialization.Serializable

data class TrackStatus(
    val id: String,
    val status: TrackStatusEnum,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
)

@Serializable
data class TrackStatusEntity(
    val id: String,
    val status: TrackStatusEnum,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
)

@Serializable
data class MediaEntity(
    val id: String,
    val name: String,
    val coverUrl: String,
    val type: MediaType,
    val trackStatus: TrackStatusEntity?,
)

enum class TrackStatusEnum {
    CONSUMING, CONSUMED, DROPPED, CATALOG, NONE
}

fun TrackStatus.toEntity() = TrackStatusEntity(
    id = id,
    status = status,
    timestamp = timestamp,
    rating = rating,
    review = review,
)

fun TrackStatusEntity.toDomain() = TrackStatus(
    id = id,
    status = status,
    timestamp = timestamp,
    rating = rating,
    review = review,
)