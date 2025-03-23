package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class Entry(
    val status: TrackStatus,
    val rating: Double? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,
    val timestamp: Long,
) : CommonParcelable