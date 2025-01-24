package de.ashman.ontrack.db.entity

import de.ashman.ontrack.domain.TrackStatusType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TrackStatusEntity(
    val statusType: TrackStatusType?,
    val rating: Int?,
    val reviewTitle: String?,
    val reviewDescription: String?,
    val timestamp: LocalDateTime,
)
