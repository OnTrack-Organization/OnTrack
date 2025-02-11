package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class TrackingHistoryEntry(
    val status: TrackStatus,
    val timestamp: Long,
) : CommonParcelable