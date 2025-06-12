package de.ashman.ontrack

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController() = ComposeUIViewController { App() }

lateinit var IOSBanner: () -> UIViewController

fun generateIOSBanner(): UIViewController {
    return IOSBanner()
}