package de.ashman.ontrack.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TrackingDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("tracking_database.db")

    return Room.databaseBuilder<TrackingDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}