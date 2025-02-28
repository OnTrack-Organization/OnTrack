package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.db.entity.tracking.EntryEntity
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class Entry(
    val status: TrackStatus,
    val timestamp: Long,
) : CommonParcelable {
    fun toEntity(): EntryEntity = EntryEntity(
        status = status,
        timestamp = timestamp,
    )
}