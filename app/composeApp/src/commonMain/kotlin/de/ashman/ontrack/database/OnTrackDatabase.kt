package de.ashman.ontrack.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import de.ashman.ontrack.database.review.ReviewDao
import de.ashman.ontrack.database.review.ReviewEntity
import de.ashman.ontrack.database.tracking.TrackingDao
import de.ashman.ontrack.database.tracking.TrackingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [TrackingEntity::class, ReviewEntity::class],
    version = 1
)
@ConstructedBy(OnTrackDatabaseConstructor::class)
abstract class OnTrackDatabase : RoomDatabase() {
    abstract fun getTrackingDao(): TrackingDao
    abstract fun getReviewDao(): ReviewDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object OnTrackDatabaseConstructor : RoomDatabaseConstructor<OnTrackDatabase> {
    override fun initialize(): OnTrackDatabase
}

fun getOnTrackDatabase(builder: RoomDatabase.Builder<OnTrackDatabase>): OnTrackDatabase {
    return builder
        //.setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}