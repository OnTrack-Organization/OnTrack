package de.ashman.ontrack.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking")
data class NewTrackingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
)