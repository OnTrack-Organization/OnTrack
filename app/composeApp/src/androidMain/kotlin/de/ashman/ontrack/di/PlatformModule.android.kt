package de.ashman.ontrack.di

import de.ashman.ontrack.database.OnTrackDatabase
import de.ashman.ontrack.database.getDatabaseBuilder
import de.ashman.ontrack.database.getOnTrackDatabase
import de.ashman.ontrack.datastore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDataStore(androidContext()) }

    single<OnTrackDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getOnTrackDatabase(builder)
    }
}