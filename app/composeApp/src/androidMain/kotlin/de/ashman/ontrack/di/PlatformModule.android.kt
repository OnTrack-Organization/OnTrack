package de.ashman.ontrack.di

import de.ashman.ontrack.database.TrackingDatabase
import de.ashman.ontrack.database.getDatabaseBuilder
import de.ashman.ontrack.database.getTrackingDatabase
import de.ashman.ontrack.datastore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }

    single<TrackingDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getTrackingDatabase(builder)
    }
}