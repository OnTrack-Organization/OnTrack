package de.ashman.ontrack.database.tracking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.ashman.ontrack.domain.media.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {
    @Query("SELECT * FROM tracking")
    fun getAll(): Flow<List<NewTrackingEntity>>

    @Query("SELECT * FROM tracking WHERE media_id = :mediaId AND media_type = :mediaType LIMIT 1")
    fun get(mediaId: String, mediaType: MediaType): Flow<NewTrackingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(tracking: NewTrackingEntity)

    @Insert
    suspend fun add(trackings: List<NewTrackingEntity>)

    @Query("DELETE FROM tracking WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM tracking")
    suspend fun deleteAll()
}