package de.ashman.ontrack.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {
    @Query("SELECT * FROM tracking")
    fun getTrackings(): Flow<List<NewTrackingEntity>>

    // TODO ugh fix and decide
    @Query("SELECT * FROM tracking WHERE tracking.id = :mediaId")
    fun getTracking(mediaId: String?): Flow<NewTrackingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTracking(tracking: NewTrackingEntity)

    @Insert
    suspend fun addTrackings(trackings: List<NewTrackingEntity>)

    @Query("DELETE FROM tracking WHERE id = :id")
    suspend fun deleteTracking(id: String)
}