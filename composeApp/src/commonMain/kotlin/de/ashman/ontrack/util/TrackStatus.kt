package de.ashman.ontrack.util

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
    val type: MediaTypeEnum,
    val trackStatus: TrackStatusEntity?,
)

enum class TrackStatusEnum {
    CONSUMING, CONSUMED, DROPPED, CATALOG, NONE
}

enum class MediaTypeEnum {
    MOVIE, SHOW, BOOK, VIDEOGAME, BOARDGAME, ALBUM
}