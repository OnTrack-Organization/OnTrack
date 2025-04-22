package de.ashman.ontrack.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [NewTrackingEntity::class],
    version = 1
)
@ConstructedBy(TrackingDatabaseConstructor::class)
abstract class TrackingDatabase : RoomDatabase() {
    abstract fun getTrackingDao(): TrackingDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TrackingDatabaseConstructor : RoomDatabaseConstructor<TrackingDatabase> {
    override fun initialize(): TrackingDatabase
}

fun getTrackingDatabase(builder: RoomDatabase.Builder<TrackingDatabase>): TrackingDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}