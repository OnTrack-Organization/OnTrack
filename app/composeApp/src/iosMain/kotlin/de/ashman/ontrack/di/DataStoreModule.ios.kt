package de.ashman.ontrack.di

import de.ashman.ontrack.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataStoreModule: Module
    get() = module { single { createDataStore() } }