package de.ashman.ontrack.domain.sub

data class TrackStatus(
    val id: String,
    val status: TrackStatusEnum,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
)

enum class TrackStatusEnum {
    CONSUMING, CONSUMED, DROPPED, CATALOG
}