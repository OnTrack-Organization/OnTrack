package de.ashman.ontrack.database.review

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE trackingId = :trackingId LIMIT 1")
    fun get(trackingId: String?): Flow<ReviewEntity?>

    @Insert
    suspend fun add(reviews: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(review: ReviewEntity)

    @Query("DELETE FROM review")
    suspend fun deleteAll()
}