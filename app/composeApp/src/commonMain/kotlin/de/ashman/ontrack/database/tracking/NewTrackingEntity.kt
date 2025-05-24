package de.ashman.ontrack.database.tracking

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.ashman.ontrack.domain.media.MediaData
import de.ashman.ontrack.domain.tracking.NewTracking
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

fun NewTrackingEntity.toDomain() = NewTracking(
    id = id,
    userId = userId,
    media = media,
    status = status,
    timestamp = timestamp,
)

