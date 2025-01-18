package de.ashman.ontrack.domain.sub

data class TrackStatus(
    val id: String,
    val status: TrackStatusType,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
)

enum class TrackStatusType {
    CONSUMING, CONSUMED, DROPPED, CATALOG
}