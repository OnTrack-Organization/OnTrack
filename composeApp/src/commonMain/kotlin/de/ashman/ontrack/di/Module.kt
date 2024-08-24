package de.ashman.ontrack.di

import de.ashman.ontrack.data.OrderViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule =
    module {
        // Define your shared dependencies here
        viewModelDefinition { OrderViewModel() }
    }

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }
