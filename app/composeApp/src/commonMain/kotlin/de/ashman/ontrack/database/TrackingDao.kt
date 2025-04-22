package de.ashman.ontrack.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {
    @Query("SELECT * FROM tracking")
    fun getTrackings(): Flow<List<NewTrackingEntity>>

    @Insert
    suspend fun addTracking(tracking: NewTrackingEntity)

    @Query("DELETE FROM tracking WHERE id = :id")
    suspend fun deleteTracking(id: Int)
}