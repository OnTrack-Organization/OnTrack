package de.ashman.ontrack.domain.sub

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class TrackStatus(
    val id: String,
    val status: TrackStatusType,
    val timestamp: Long,
    val rating: Int?,
    val review: String?,
) : CommonParcelable

enum class TrackStatusType {
    CONSUMING, CONSUMED, DROPPED, CATALOG
}