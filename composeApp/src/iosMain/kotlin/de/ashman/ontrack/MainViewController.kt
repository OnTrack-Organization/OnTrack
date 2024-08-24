package de.ashman.ontrack

import App
import androidx.compose.ui.window.ComposeUIViewController
import de.ashman.ontrack.di.initKoin

fun MainViewController() =
    ComposeUIViewController {
        initKoin { }
        App()
    }
