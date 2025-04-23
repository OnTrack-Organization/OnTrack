package de.ashman.ontrack.database

import androidx.room.TypeConverter
import de.ashman.ontrack.domain.newdomains.MediaData
import de.ashman.ontrack.domain.tracking.TrackStatus
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun mediaDataToString(mediaData: MediaData): String = Json.encodeToString(mediaData)

    @TypeConverter
    fun stringToMediaData(value: String): MediaData = Json.decodeFromString(value)

    @TypeConverter
    fun trackStatusToString(status: TrackStatus): String = status.name

    @TypeConverter
    fun stringToTrackStatus(value: String): TrackStatus = TrackStatus.valueOf(value)
}
