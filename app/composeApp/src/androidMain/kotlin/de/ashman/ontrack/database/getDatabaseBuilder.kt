package de.ashman.ontrack.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<OnTrackDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("ontrack_database.db")

    return Room
        .databaseBuilder<OnTrackDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
        .setDriver(AndroidSQLiteDriver())
}