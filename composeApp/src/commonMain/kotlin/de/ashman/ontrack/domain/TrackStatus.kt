package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class TrackStatus(
    val statusType: TrackStatusType? = null,
    val rating: Int? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,
    val timestamp: String? = null,
) : CommonParcelable

enum class TrackStatusType {
    CONSUMING, CONSUMED, DROPPED, CATALOG
}

const val MAX_RATING = 5