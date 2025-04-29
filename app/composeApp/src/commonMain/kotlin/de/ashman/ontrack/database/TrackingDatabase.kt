package de.ashman.ontrack.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
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
        //.setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}