package de.ashman.ontrack.database.tracking

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.ashman.ontrack.domain.media.MediaData
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking

@Entity(tableName = "tracking")
data class TrackingEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    @Embedded(prefix = "media_")
    val media: MediaData,
    val status: TrackStatus,
    val timestamp: Long,
)

fun TrackingEntity.toDomain() = Tracking(
    id = id,
    userId = userId,
    media = media,
    status = status,
    timestamp = timestamp,
)

