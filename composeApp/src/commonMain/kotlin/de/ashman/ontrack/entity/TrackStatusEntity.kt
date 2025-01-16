package de.ashman.ontrack.entity

import de.ashman.ontrack.domain.sub.TrackStatusEnum
import kotlinx.serialization.Serializable

@Serializable
data class TrackStatusEntity(
    val id: String,
    val status: TrackStatusEnum,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
)
