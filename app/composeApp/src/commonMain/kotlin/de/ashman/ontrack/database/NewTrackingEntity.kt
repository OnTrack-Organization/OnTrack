package de.ashman.ontrack.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.ashman.ontrack.domain.newdomains.MediaData
import de.ashman.ontrack.domain.tracking.TrackStatus

@Entity(tableName = "tracking")
data class NewTrackingEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    @Embedded(prefix = "media_")
    val media: MediaData,
    val status: TrackStatus,
    val timestamp: Long,
)